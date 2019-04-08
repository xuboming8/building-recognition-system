# coding:utf8 
import os
from PIL import Image,ImageFilter
import numpy as np
import math
import matplotlib.pyplot as plt
import matplotlib
import time

def gaussian_blur(image_array, radius):
    #1.权重矩阵
    weight_matrix = np.zeros((2*radius+1, 2*radius+1))
    for i in range(2*radius+1):
        for j in range(2*radius+1):
            weight_matrix[i, j] = GussianFunction2D(i-radius, j-radius)
    weight_matrix /= weight_matrix.sum()

    #2.根据权重矩阵weight_matrix更新新的图像像素值
    image_array_gau = np.zeros(image_array.shape)
    for i in range(image_array.shape[0]-2*radius):
        for j in range(image_array.shape[1]-2*radius):
            for k in range(3):
                # 时间完全耗在这一步运算上了
                a = np.multiply(weight_matrix, image_array[i:(i+2*radius+1), j:(j+2*radius+1), k])
                image_array_gau[i+radius, j+radius, k] = a.sum()
    return image_array_gau/255

# 计算二维高斯函数的值G(x, y)
def GussianFunction2D(x, y):
    sigma = 1.5 #sigma为标准差，sigma越大, 高斯函数图像越扁, 则模糊效果越明显; sigma越小, 趋向0的话, 则模糊效果越差越接近真实图像
    return (1/(2*math.pi*sigma**2)) * math.e**(-(x**2 + y**2)/(2*sigma**2))
 
filename = os.listdir("./test_mohu&vedio/")
base_dir = "./test_mohu&vedio/"
new_dir  = "./test_mohu&vedio/"
 
for img in filename:
    image = Image.open(base_dir + img)
    image_array = np.array(image)
    image_array_gau = gaussian_blur(image_array, 5)
    # plt.figure('GaussianBlur')
    # plt.imshow(image_array_gau)
    # plt.show()
    # image_mohu = image.filter(ImageFilter.BLUR)
    matplotlib.image.imsave(new_dir+ img, image_array_gau)
    #image_array_gau.save(new_dir+ img)
    