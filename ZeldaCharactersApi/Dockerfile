FROM python:3.11-slim

WORKDIR /app

# Install dependencies
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Copy proto file
COPY proto/ ./proto/

# Create src directory and generate gRPC code
RUN mkdir -p ./src && \
    python -m grpc_tools.protoc -I./proto --python_out=./src --grpc_python_out=./src ./proto/characters.proto

# Copy application code
COPY src/ ./src/

# Expose gRPC port
EXPOSE 50051

# Run the server
CMD ["python", "src/server.py"]
