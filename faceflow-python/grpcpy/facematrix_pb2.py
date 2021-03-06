# Generated by the protocol buffer compiler.  DO NOT EDIT!
# source: facematrix.proto

import sys
_b=sys.version_info[0]<3 and (lambda x:x) or (lambda x:x.encode('latin1'))
from google.protobuf import descriptor as _descriptor
from google.protobuf import message as _message
from google.protobuf import reflection as _reflection
from google.protobuf import symbol_database as _symbol_database
# @@protoc_insertion_point(imports)

_sym_db = _symbol_database.Default()




DESCRIPTOR = _descriptor.FileDescriptor(
  name='facematrix.proto',
  package='facematrix',
  syntax='proto2',
  serialized_options=_b('\n\026com.example.facematrixB\nFaceMatrix'),
  serialized_pb=_b('\n\x10\x66\x61\x63\x65matrix.proto\x12\nfacematrix\"\x14\n\x04\x46\x61\x63\x65\x12\x0c\n\x04\x66\x61\x63\x65\x18\x03 \x02(\x0c\"\x1c\n\x06Matrix\x12\x12\n\x06matrix\x18\x04 \x03(\x01\x42\x02\x10\x01\x32\x44\n\rFaceTransform\x12\x33\n\tgetMatrix\x12\x10.facematrix.Face\x1a\x12.facematrix.Matrix\"\x00\x42$\n\x16\x63om.example.facematrixB\nFaceMatrix')
)




_FACE = _descriptor.Descriptor(
  name='Face',
  full_name='facematrix.Face',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  fields=[
    _descriptor.FieldDescriptor(
      name='face', full_name='facematrix.Face.face', index=0,
      number=3, type=12, cpp_type=9, label=2,
      has_default_value=False, default_value=_b(""),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
  ],
  extensions=[
  ],
  nested_types=[],
  enum_types=[
  ],
  serialized_options=None,
  is_extendable=False,
  syntax='proto2',
  extension_ranges=[],
  oneofs=[
  ],
  serialized_start=32,
  serialized_end=52,
)


_MATRIX = _descriptor.Descriptor(
  name='Matrix',
  full_name='facematrix.Matrix',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  fields=[
    _descriptor.FieldDescriptor(
      name='matrix', full_name='facematrix.Matrix.matrix', index=0,
      number=4, type=1, cpp_type=5, label=3,
      has_default_value=False, default_value=[],
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=_b('\020\001'), file=DESCRIPTOR),
  ],
  extensions=[
  ],
  nested_types=[],
  enum_types=[
  ],
  serialized_options=None,
  is_extendable=False,
  syntax='proto2',
  extension_ranges=[],
  oneofs=[
  ],
  serialized_start=54,
  serialized_end=82,
)

DESCRIPTOR.message_types_by_name['Face'] = _FACE
DESCRIPTOR.message_types_by_name['Matrix'] = _MATRIX
_sym_db.RegisterFileDescriptor(DESCRIPTOR)

Face = _reflection.GeneratedProtocolMessageType('Face', (_message.Message,), dict(
  DESCRIPTOR = _FACE,
  __module__ = 'facematrix_pb2'
  # @@protoc_insertion_point(class_scope:facematrix.Face)
  ))
_sym_db.RegisterMessage(Face)

Matrix = _reflection.GeneratedProtocolMessageType('Matrix', (_message.Message,), dict(
  DESCRIPTOR = _MATRIX,
  __module__ = 'facematrix_pb2'
  # @@protoc_insertion_point(class_scope:facematrix.Matrix)
  ))
_sym_db.RegisterMessage(Matrix)


DESCRIPTOR._options = None
_MATRIX.fields_by_name['matrix']._options = None

_FACETRANSFORM = _descriptor.ServiceDescriptor(
  name='FaceTransform',
  full_name='facematrix.FaceTransform',
  file=DESCRIPTOR,
  index=0,
  serialized_options=None,
  serialized_start=84,
  serialized_end=152,
  methods=[
  _descriptor.MethodDescriptor(
    name='getMatrix',
    full_name='facematrix.FaceTransform.getMatrix',
    index=0,
    containing_service=None,
    input_type=_FACE,
    output_type=_MATRIX,
    serialized_options=None,
  ),
])
_sym_db.RegisterServiceDescriptor(_FACETRANSFORM)

DESCRIPTOR.services_by_name['FaceTransform'] = _FACETRANSFORM

# @@protoc_insertion_point(module_scope)
