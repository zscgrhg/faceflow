# Copyright 2015 gRPC authors.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
"""The Python implementation of the GRPC helloworld.Greeter server."""

import time
from concurrent import futures

import grpc

import facematrix.FaceWork as FaceWork
import grpcpy.facematrix_pb2 as facematrix_pb2
import grpcpy.facematrix_pb2_grpc as facematrix_pb2_grpc
import os
import tempfile

_ONE_DAY_IN_SECONDS = 60 * 60 * 24


class FaceTransformServicerImpl(facematrix_pb2_grpc.FaceTransformServicer):

    def getMatrix(self, request, context):

        fd, path = tempfile.mkstemp(dir="../image")
        try:
            with os.fdopen(fd, 'wb') as tmp:
                # do stuff with temp file
                tmp.write(request.face)
                transform = FaceWork.transform(path)
                print(path)
                return facematrix_pb2.Matrix(matrix=transform)
        finally:
            os.remove(path)


def serve():
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
    facematrix_pb2_grpc.add_FaceTransformServicer_to_server(FaceTransformServicerImpl(), server)
    server.add_insecure_port('[::]:50051')
    server.start()
    try:
        while True:
            time.sleep(_ONE_DAY_IN_SECONDS)
    except KeyboardInterrupt:
        server.stop(0)


if __name__ == '__main__':
    serve()
