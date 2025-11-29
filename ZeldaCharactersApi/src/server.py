import os
import grpc
from concurrent import futures
import logging

import characters_pb2_grpc
from servicer import CharacterServicer

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)


def serve():
    port = os.getenv('GRPC_PORT', '50051')
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
    
    characters_pb2_grpc.add_CharacterServiceServicer_to_server(
        CharacterServicer(), server
    )
    
    server.add_insecure_port(f'[::]:{port}')
    server.start()
    
    logger.info(f"🚀 gRPC Server started on port {port}")
    logger.info("Ready to accept connections...")
    
    try:
        server.wait_for_termination()
    except KeyboardInterrupt:
        logger.info("Shutting down server...")
        server.stop(0)


if __name__ == '__main__':
    serve()
