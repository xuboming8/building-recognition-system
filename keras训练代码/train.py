import os
os.environ['TF_CPP_MIN_LOG_LEVEL']='2'
import numpy as np
from keras.preprocessing import image
from keras.applications.mobilenet import MobileNet,preprocess_input, decode_predictions
from keras.layers import Dense, Dropout, Flatten,AveragePooling2D
from keras.models import Model,Sequential, model_from_yaml, load_model
from keras.utils import np_utils
from sklearn.model_selection import train_test_split
from keras.optimizers import Adam, SGD
from keras import callbacks
import matplotlib.pyplot as plt
from keras.preprocessing.image import ImageDataGenerator


def mobilenet_model():
    mobile = MobileNet(include_top=False, weights ='imagenet', input_shape=[224,224,3])
    for layer in mobile.layers:
        layer.trainable = False
    last = mobile.output
    x = AveragePooling2D((7, 7), strides=(1, 1), padding='valid')(last)
    x = Flatten()(x)
    x = Dropout(0.4)(x)
    x = Dense(512, activation='relu')(x)
    x = Dropout(0.4)(x)
    x = Dense(26, activation='softmax')(x)
    model = Model(inputs=mobile.input, outputs=x)
    return model


def pred_data():

    model=load_model('my_model.h5')
    # with open('./building.yaml') as yamlfile:
    #     loaded_model_yaml = yamlfile.read()
    # model = model_from_yaml(loaded_model_yaml)
    # model.load_weights('./building.h5')

    sgd = Adam(lr=0.0001)
    model.compile(loss='categorical_crossentropy',optimizer=sgd, metrics=['accuracy'])

    path='./test_mohu&vedio(m)/'
    num1 = float(0)
    num2 = float(0)
    for f in os.listdir(path):
        img = image.load_img(path + f, target_size=(224,224))
        img_array = image.img_to_array(img)
        x = np.expand_dims(img_array, axis=0)
        x = preprocess_input(x)
        result = model.predict(x,verbose=0)
        result=np.argmax(result)
        num1+=1
        if 'A' in f and result==0:
            num2+=1
        if 'aifo' in f and result==1:
            num2+=1
        if 'bali' in f and result==2:
            num2+=1
        if 'bdlg' in f and result==3:
            num2+=1
        if 'benzhong' in f and result==4:
            num2+=1
        if 'bisa' in f and result==5:
            num2+=1
        if 'bjdkc' in f and result==6:
            num2+=1
        if 'blue' in f and result==7:
            num2+=1
        if 'dfmz' in f and result==8:
            num2+=1
        if 'dibai' in f and result==9:
            num2+=1
        if 'double' in f and result==10:
            num2+=1
        if 'freedom' in f and result==11:
            num2+=1
        if 'gg' in f and result==12:
            num2+=1
        if 'gzt' in f and result==13:
            num2+=1
        if 'hebdjy' in f and result==14:
            num2+=1
        if 'hzxldjd' in f and result==15:
            num2+=1
        if 'jiaodou' in f and result==16:
            num2+=1
        if 'lufu' in f and result==17:
            num2+=1
        if 'mali' in f and result==18:
            num2+=1
        if 'nc' in f and result==19:
            num2+=1
        if 'shengsuo' in f and result==20:
            num2+=1
        if 'soho' in f and result==21:
            num2+=1
        if 'taiji' in f and result==22:
            num2+=1
        if 'wuge' in f and result==23:
            num2+=1
        if 'xini' in f and result==24:
            num2+=1
        if 'yasheng' in f and result==25:
            num2+=1
        
        print(f,result)   
    print(num2/num1)


font1 = {'family': 'Times New Roman','color':  'black','weight': 'normal','size': 40,
        }     

def training_vis(hist):
    loss = hist.history['loss']
    val_loss = hist.history['val_loss']
    acc = hist.history['acc']
    val_acc = hist.history['val_acc']

    # make a figure
    fig = plt.figure(figsize=(20,10))
    # subplot loss
    ax1 = fig.add_subplot(121)
    ax1.plot(loss,label='train_loss',linewidth=5.0)
    ax1.plot(val_loss,label='val_loss',linewidth=5.0)
    ax1.set_xlabel('Epochs',fontdict=font1)
    ax1.set_ylabel('Loss',fontdict=font1)
    ax1.set_title('Loss on Training and Validation Data',fontsize=40)
    ax1.legend()
    # subplot acc
    ax2 = fig.add_subplot(122)
    ax2.plot(acc,label='train_acc',linewidth=5.0)
    ax2.plot(val_acc,label='val_acc',linewidth=5.0)
    ax2.set_xlabel('Epochs',fontdict=font1)
    ax2.set_ylabel('Accuracy',fontdict=font1)
    ax2.set_title('Accuracy  on Training and Validation Data',fontsize=40)
    ax2.legend()
    plt.tight_layout()
    plt.show()


train_datagen = ImageDataGenerator(
    width_shift_range = 0.2,
    height_shift_range = 0.2,
    zoom_range = 0.3,
    rescale = 1./255,
    fill_mode='nearest',
    horizontal_flip = True,
)

test_datagen = ImageDataGenerator(rescale=1./255)

train_generator = train_datagen.flow_from_directory(
        './train',
        target_size=(224, 224),
        batch_size=32,
        class_mode='categorical')
 
validation_generator = test_datagen.flow_from_directory(
        './validation',
        target_size=(224, 224),
        batch_size=32,
        class_mode='categorical')


def trainingV1():
    model=load_model('my_model.h5')
    #model = mobilenet_model()
    sgd = Adam(lr=0.0001)
    model.compile(loss='categorical_crossentropy',optimizer=sgd, metrics=['accuracy'])
    
    history = model.fit_generator(
        train_generator,
        steps_per_epoch = 20,
        epochs = 50,
        verbose = 1,
        validation_data = validation_generator,
        validation_steps = 20
        )

    yaml_string = model.to_yaml()
    with open('./building.yaml', 'w') as outfile:
        outfile.write(yaml_string)
    model.save_weights('./building.h5')
    model.save('my_model.h5')

    training_vis(history)


def trainingV2():
    model=load_model('my_model.h5')
    #sgd = SGD(lr=0.01, decay=1e-6, momentum=0.9, nesterov=True)
    #plot_model(model, to_file='model.png')
    for layer in model.layers[:67]:
        layer.trainable = False
    for layer in model.layers[68:]:
        layer.trainable = True
    model.compile(loss='categorical_crossentropy', optimizer=Adam(0.0001), metrics=['accuracy'])
    history = model.fit_generator(
        train_generator,
        steps_per_epoch = 20,
        epochs = 100,
        verbose = 1,
        validation_data = validation_generator,
        validation_steps = 20
        )
    
    yaml_string = model.to_yaml()
    with open('./building.yaml', 'w') as outfile:
        outfile.write(yaml_string)
    model.save_weights('./building.h5')
    model.save('my_model.h5')

    training_vis(history)


def trainingV3():
    model=load_model('my_model.h5')
    #sgd = SGD(lr=0.01, decay=1e-6, momentum=0.9, nesterov=True)
    #plot_model(model, to_file='model.png')
    for layer in model.layers[:49]:
        layer.trainable = False
    for layer in model.layers[50:67]:
        layer.trainable = True

    for layer in model.layers[68:]:
        layer.trainable = False

    # for layer in model.layers[68:86]:
    #     layer.trainable = False
    # for layer in model.layers[87:]:
    #     layer.trainable = True

    model.compile(loss='categorical_crossentropy', optimizer=Adam(0.0001), metrics=['accuracy'])
    history = model.fit_generator(
        train_generator,
        steps_per_epoch = 20,
        epochs = 20,
        verbose = 1,
        validation_data = validation_generator,
        validation_steps = 20
        )
    
    yaml_string = model.to_yaml()
    with open('./building.yaml', 'w') as outfile:
        outfile.write(yaml_string)
    model.save_weights('./building.h5')
    model.save('my_model.h5')

    training_vis(history)


def trainingV4():
    model=load_model('my_model.h5')
    #sgd = SGD(lr=0.01, decay=1e-6, momentum=0.9, nesterov=True)
    #plot_model(model, to_file='model.png')
    for layer in model.layers[:61]:
        layer.trainable = False
    for layer in model.layers[62:67]:
        layer.trainable = True

    for layer in model.layers[68:]:
        layer.trainable = False

    # for layer in model.layers[68:86]:
    #     layer.trainable = False
    # for layer in model.layers[87:]:
    #     layer.trainable = True

    model.compile(loss='categorical_crossentropy', optimizer=Adam(0.0001), metrics=['accuracy'])
    history = model.fit_generator(
        train_generator,
        steps_per_epoch = 20,
        epochs = 20,
        verbose = 1,
        validation_data = validation_generator,
        validation_steps = 20
        )
    
    yaml_string = model.to_yaml()
    with open('./building.yaml', 'w') as outfile:
        outfile.write(yaml_string)
    model.save_weights('./building.h5')
    model.save('my_model.h5')

    training_vis(history)



def show():
    model=load_model('my_model.h5')
    #model = mobilenet_model()
    #model = MobileNet(include_top=True, weights ='imagenet', input_shape=(224,224,3))
    model.summary()
    for i, layer in enumerate(model.layers):
        print(i, layer.name)


if __name__ == '__main__':
    
    #trainingV1()
    
    #pred_data()

    #trainingV2()

    #pred_data()

    #trainingV3()

    pred_data()

    #trainingV4()

    #pred_data()

    #show()