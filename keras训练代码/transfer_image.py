# coding:utf8 
import os
from PIL import Image
 
filename = os.listdir("./building_show/")
base_dir = "./building_show/"
new_dir  = "./building_show/"
 
for img in filename:
    image = Image.open(base_dir + img)
    image_size = image.resize((224, 224),Image.ANTIALIAS)
    image_size.save(new_dir+ img)
    #image_ro = image.rotate(270)
    #image_ro.save(new_dir+ img)