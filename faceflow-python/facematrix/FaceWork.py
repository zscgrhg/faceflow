# -*- coding: utf-8 -*-
# CLASSWORLDS
# 2018/11/25

from __future__ import absolute_import
from __future__ import division
from __future__ import print_function

import argparse
import copy
import os

import numpy as np
import tensorflow as tf
from facenet.src import align
from facenet.src import facenet
from facenet.src.align import detect_face
from scipy import misc

tf.Graph().as_default()
sess = tf.Session()
facenet.load_model("../model/20180408-102900.pb")
images_placeholder = tf.get_default_graph().get_tensor_by_name("input:0")
embeddings = tf.get_default_graph().get_tensor_by_name("embeddings:0")
phase_train_placeholder = tf.get_default_graph().get_tensor_by_name("phase_train:0")

pnet, rnet, onet = align.detect_face.create_mtcnn(sess, None)

print("load model success!")

def main(args):
    images = load_and_align_data(args.image_files, args.image_size, args.margin, args.gpu_memory_fraction)
    # Load the model

    # Get input and output tensors

    # Run forward pass to calculate embeddings
    feed_dict = {images_placeholder: images, phase_train_placeholder: False}
    emb = sess.run(embeddings, feed_dict=feed_dict)

    nrof_images = len(args.image_files)

    print('Images:')
    for i in range(nrof_images):
        print('%1d: %s' % (i, args.image_files[i]))
    print('')

    # Print distance matrix
    print('Distance matrix')
    print('    ', end='')
    for i in range(nrof_images):
        print('    %1d     ' % i, end='')
    print('')

    return emb[0,:]
    # retMatrix = []
    # for i in range(nrof_images):
    #     print('%1d  ' % i, end='')
    #     for j in range(nrof_images):
    #         dist = np.sqrt(np.sum(np.square(np.subtract(emb[i, :], emb[j, :]))))
    #         retMatrix.append((i, j, dist))
    #         print('  %1.4f  ' % dist, end='')
    #     print('')
    # return retMatrix


def load_and_align_data(image_paths, image_size, margin, gpu_memory_fraction):
    minsize = 20  # minimum size of face
    threshold = [0.6, 0.7, 0.7]  # three steps's threshold
    factor = 0.709  # scale factor

    print('Creating networks and loading parameters')

    tmp_image_paths = copy.copy(image_paths)
    img_list = []
    for image in tmp_image_paths:
        img = misc.imread(os.path.expanduser(image), mode='RGB')
        img_size = np.asarray(img.shape)[0:2]
        bounding_boxes, _ = align.detect_face.detect_face(img, minsize, pnet, rnet, onet, threshold, factor)
        if len(bounding_boxes) < 1:
            image_paths.remove(image)
            print("can't detect face, remove ", image)
            continue
        det = np.squeeze(bounding_boxes[0, 0:4])
        bb = np.zeros(4, dtype=np.int32)
        bb[0] = np.maximum(det[0] - margin / 2, 0)
        bb[1] = np.maximum(det[1] - margin / 2, 0)
        bb[2] = np.minimum(det[2] + margin / 2, img_size[1])
        bb[3] = np.minimum(det[3] + margin / 2, img_size[0])
        cropped = img[bb[1]:bb[3], bb[0]:bb[2], :]
        aligned = misc.imresize(cropped, (image_size, image_size), interp='bilinear')
        # misc.imsave("oops", aligned)
        prewhitened = facenet.prewhiten(aligned)
        img_list.append(prewhitened)
    images = np.stack(img_list)
    return images


def parse_arguments(argv):
    parser = argparse.ArgumentParser()

    parser.add_argument('model', type=str,
                        help='Could be either a directory containing the meta_file and ckpt_file or a model protobuf (.pb) file')
    parser.add_argument('image_files', type=str, nargs='+', help='Images to compare')
    parser.add_argument('--image_size', type=int,
                        help='Image size (height, width) in pixels.', default=160)
    parser.add_argument('--margin', type=int,
                        help='Margin for the crop around the bounding box (height, width) in pixels.', default=44)
    parser.add_argument('--gpu_memory_fraction', type=float,
                        help='Upper bound on the amount of GPU memory that will be used by the process.', default=1.0)
    return parser.parse_args(argv)


def save(image, simpleName, image_size=160, margin=44, gpu_memory_fraction=1.0):
    minsize = 20  # minimum size of face
    threshold = [0.6, 0.7, 0.7]  # three steps's threshold
    factor = 0.709  # scale factor

    print('Creating networks and loading parameters')

    img = misc.imread(os.path.expanduser(image), mode='RGB')
    img_size = np.asarray(img.shape)[0:2]
    bounding_boxes, _ = align.detect_face.detect_face(img, minsize, pnet, rnet, onet, threshold, factor)
    if len(bounding_boxes) < 1:
        print("can't detect face from ", image)
        return
    det = np.squeeze(bounding_boxes[0, 0:4])
    bb = np.zeros(4, dtype=np.int32)
    bb[0] = np.maximum(det[0] - margin / 2, 0)
    bb[1] = np.maximum(det[1] - margin / 2, 0)
    bb[2] = np.minimum(det[2] + margin / 2, img_size[1])
    bb[3] = np.minimum(det[3] + margin / 2, img_size[0])
    cropped = img[bb[1]:bb[3], bb[0]:bb[2], :]
    aligned = misc.imresize(cropped, (image_size, image_size), interp='bilinear')
    prewhitened = facenet.prewhiten(aligned)
    misc.imsave("static/facedb/" + simpleName, aligned)
    misc.imsave("static/facedbpw/" + simpleName, prewhitened)


def transform(face1):
    return main(parse_arguments(["../model/20180408-102900.pb", face1]))

# main(parse_arguments(["model/20180408-102900.pb","face/hx1.jpg","face/hx2.jpg"]))
