import sys
import os
import os.path as osp
from keras import backend as K
from keras.models import load_model
from tensorflow.python.framework import graph_io
import tensorflow as tf
def freeze_session(session, keep_var_names=None, output_names=None, clear_devices=True):
    from tensorflow.python.framework.graph_util import convert_variables_to_constants
    graph = session.graph
    with graph.as_default():
        freeze_var_names = list(set(v.op.name for v in tf.global_variables()).difference(keep_var_names or []))
        output_names = output_names or []
        output_names += [v.op.name for v in tf.global_variables()]
        input_graph_def = graph.as_graph_def()
        if clear_devices:
            for node in input_graph_def.node:
                node.device = ""
        frozen_graph = convert_variables_to_constants(session, input_graph_def,
                                                      output_names, freeze_var_names)
        return frozen_graph

weight_file_name = 'my_model.h5'
output_graph_name = 'my_model.pb'
# input_fld = '~/Animal/'
output_fld = 'tensorflow_model/'
# if not os.path.isdir(output_fld):
#     os.mkdir(output_fld)

K.set_learning_phase(0)
net_model = load_model('my_model.h5')

print('input is :', net_model.input.name)
print('output is:', net_model.output.name)

sess = K.get_session()
frozen_graph = freeze_session(K.get_session(), output_names=[net_model.output.op.name])
graph_io.write_graph(frozen_graph, output_fld, output_graph_name, as_text=False)

print('saved the constant graph (ready for inference) at: ', osp.join(output_fld, output_graph_name))
