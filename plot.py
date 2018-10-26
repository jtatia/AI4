import matplotlib.pyplot as plt
import numpy as np

y,x = np.loadtxt('file1.txt', delimiter=',', unpack=True)
plt.plot(x,y, label='Graph')

plt.xlabel('Theta')
plt.ylabel('Current')
plt.title('Current vs Theta')
plt.legend()
plt.show()

y,x = np.loadtxt('file2.txt', delimiter=',', unpack=True)
plt.plot(x,y, label='Graph')

plt.xlabel('Omega')
plt.ylabel('Current')
plt.title('Current vs Omega')
plt.legend()
plt.show()