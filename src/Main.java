import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Scanner;

public class Main {

    static class Point {
        double x;
        double y;
        Point(double x,double y) {
            this.x = x;
            this.y = y;
        }
    }

    private ArrayList<ArrayList<Point>> thetaProfiles;
    private ArrayList<ArrayList<Point>> omegaProfiles;
    private ArrayList<ArrayList<Point>> currentProfiles;
    private ArrayList<ArrayList<Integer>> controlMatrix;
    private int proportionality;
    private int M;
    private int R;

    /**
     * Order of input
     * Zero, small positive, small negative, medium positive, medium negative
     * All profiles coordinates between 0 and 1
     * For any profile,coordinates in cyclic, starting from lower left
     */
    private static ArrayList<ArrayList<Point>> takeInput(int N) {
        Scanner sc = new Scanner(System.in);
        ArrayList<ArrayList<Point>> result = new ArrayList<>();
        ArrayList<Point> arrayList = new ArrayList<>();

        System.out.println("All Profiles in cyclic order, start from lower left");
        System.out.println("Enter coordinates Of Zero Profile");
        //Taking Input for Zero Profile
        for (int i = 0; i < 3; i++) {
            double x = sc.nextDouble();
            double y = sc.nextDouble();
            Point point = new Point(x, y);
            arrayList.add(point);
        }
        result.add(arrayList);

        System.out.println("Enter coordinates of profiles in order: " +
                "small positive, small negative, medium positive, medium negative");
        //Taking input for all other profiles (assumption trapeziums)
        for (int i=0;i<N-1;i++) {
            arrayList = new ArrayList<>();
            for (int j=0;j<4;j++) {
                double x = sc.nextDouble();
                double y = sc.nextDouble();
                Point point = new Point(x, y);
                arrayList.add(point);
            }
            result.add(arrayList);

                System.out.println("Next Profile........");
        }
        return result;
    }

    public void initiate() {
        Scanner sc =  new Scanner(System.in);
        thetaProfiles = new ArrayList<>();
        omegaProfiles = new ArrayList<>();
        currentProfiles = new ArrayList<>();

        System.out.println(":::THETA PROFILES:::");
        thetaProfiles = takeInput(3);
        System.out.println(":::OMEGA PROFILES:::");
        omegaProfiles = takeInput(3);
        System.out.println(":::CURRENT PROFILES:::");
        currentProfiles = takeInput(5);
        System.out.println("Input Taken");
        //Setting Control Matrix
        controlMatrix = new ArrayList<>();
        controlMatrix.add(new ArrayList<>(Arrays.asList(0,2,1)));
        controlMatrix.add(new ArrayList<>(Arrays.asList(2,4,0)));
        controlMatrix.add(new ArrayList<>(Arrays.asList(1,0,3)));
        proportionality = 1;
        M = 1;
        R = 1;
    }

    public double fuzzy(double initialTheta, double initialOmega) {
        ArrayList<Double> thetaIntersections = findIntersections(thetaProfiles, initialTheta);
        System.out.println(thetaIntersections);
        ArrayList<Double> omegaIntersections = findIntersections(omegaProfiles, initialOmega);
        System.out.println(omegaIntersections);
        Double current = calculate(thetaIntersections, omegaIntersections, currentProfiles);
        System.out.println(current);
        double torque = proportionality*current;
        double angularAcceleration = torque/(M*R*R);
        return angularAcceleration;
    }


    private Double calculate(ArrayList<Double> thetaIntersections, ArrayList<Double> omegaIntersections, ArrayList<ArrayList<Point>> currentProfiles) {

        double centroidXSum = 0;
        double areaSum = 0;
        for (int i=0;i<thetaIntersections.size();i++) {
            ArrayList<Point> arrayList = new ArrayList<>();
            for (int j=0;j<omegaIntersections.size();j++) {
                if (thetaIntersections.get(i) != 0.0 && omegaIntersections.get(j) != 0.0) {
                    double val = Math.min(thetaIntersections.get(i), omegaIntersections.get(j));
                 //   System.out.println("i="+i+" j="+j+" min="+val);
                    //Control Matrix determines which current profile to use
                    int profileIndex = controlMatrix.get(i).get(j);
                 //   System.out.println("Profile Index = "+profileIndex);
                    ArrayList<Double> temp = findIntersection(currentProfiles.get(profileIndex), val, 'x');
                    //Creating Trapezoid
                 //   System.out.println("TEMP="+temp);
                    Point p1 = currentProfiles.get(profileIndex).get(0);
                    arrayList.add(p1);
                    arrayList.add(new Point(temp.get(0), val));
                    arrayList.add(new Point(temp.get(1), val));
                    Point p2 = currentProfiles.get(profileIndex).get(currentProfiles.get(profileIndex).size()-1);
                    arrayList.add(p2);
                    //Area of Trapezoid
                    double base1 = Math.abs(temp.get(1)-temp.get(0));
                    double base2 = Math.abs(p2.x-p1.x);
                    double height = val;
                    double Area = 0.5*(base1+base2)*height;
                    double centroidX = ((p2.x*p2.x+p2.x*temp.get(1)+temp.get(1)*temp.get(1))
                            -(p1.x*p1.x+p1.x*temp.get(0)+temp.get(0)*temp.get(0)))
                            /(3*((p2.x+temp.get(1))-(p1.x+temp.get(0))));
                    areaSum += Area;
                    centroidXSum += centroidX*Area;
                }
            }
        }

        return centroidXSum/areaSum;
    }

    /**
     * Returns a triplet (intersection zero profile, intersection positive small, intersection neg small)
     */
    private ArrayList<Double> findIntersections(ArrayList<ArrayList<Point>> profiles, double initial) {
        ArrayList<Double> result = new ArrayList<>();
        for (ArrayList<Point> profile : profiles) {
            ArrayList<Double> arr = findIntersection(profile, initial, 'y');
            if (arr!=null) {
                result.addAll(arr);
            } else {
                result.add(0.0);
            }
        }
        return result;
    }

    /**
     * Given a profile, 'initial' and parallel axis, it does the following:-
     * For 'Y':
     *        For every line in profile, it finds intersection point. If found, returned, else returns null.
     * For 'X':
     *        For every line in profile, it finds intersection point. If found, added to list(unique).
     *        (Since profile can have multiple points of intersection)
     */
    private ArrayList<Double> findIntersection(ArrayList<Point> profile, double initial, char axis) {
        if (axis=='y') {
            for (int i=0;i<profile.size()-1;i++) {
                Point p1 = profile.get(i);
                Point p2 = profile.get(i+1);
                double m = slope(p2,p1);
                double intersection = p1.y+m*(initial-p1.x);
                if (intersection>0&&intersection<1)
                    return new ArrayList<>(Arrays.asList(intersection));
            }
            return null;
        } else {
            ArrayList<Double> linkedHashSet = new ArrayList<>();
            for (int i=0;i<profile.size()-1;i++) {
                Point p1 = profile.get(i);
                Point p2 = profile.get(i+1);
                double m = slope(p2,p1);
                if (m==0 && initial==p1.y) {
                    return new ArrayList<>(Arrays.asList(p1.x,p2.x));
                } else if (m==0) {
                    continue;
                } else {
           //         System.out.println("HERE!");
                    double intersectionX = p1.x+(initial-p1.y)/m;
           //         System.out.println("inter="+intersectionX);
                    linkedHashSet.add(intersectionX);
                }
            }
            return linkedHashSet;
        }
    }

    /**
     * Calculate slope for given pair of points
     */
    private double slope(Point p2, Point p1) {
        double val = (p2.y-p1.y)/(p2.x-p1.x);
        return val;
    }
}

/**
 1
 1
 -5 0
 0 1
 5 0
 0 0
 2.5 1
 5.5 1
 8 0
 -8 0
 -5.5 1
 -2.5 1
 0 0
 -1.43 0
 0 1
 1.43 0
 0 0
 1.43 1
 3.43 1
 4.86 0
 -4.86 0
 -3.43 1
 -1.43 1
 0 0
 -1 0
 0 1
 1 0
 0 0
 1 1
 3 1
 4 0
 -4 0
 -3 1
 -1 1
 0 0
 2 0
 4 1
 6 1
 8 0
 -8 0
 -6 1
 -4 1
 -2 0
 */