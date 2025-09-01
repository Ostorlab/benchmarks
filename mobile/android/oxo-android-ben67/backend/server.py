from http.server import HTTPServer, BaseHTTPRequestHandler
from datetime import datetime


class Handler(BaseHTTPRequestHandler):
    def do_POST(self):
        content_length = int(self.headers.get("Content-Length", 0))
        post_data = self.rfile.read(content_length).decode("utf-8")

        print(f"\n=== POST Request at {datetime.now()} ===")
        print(f"Client: {self.client_address[0]}")
        print(f"Path: {self.path}")
        print(f"Headers: {dict(self.headers)}")
        print(f"Body: {post_data}")
        print("=" * 50)

        # Send response
        self.send_response(200)
        self.send_header("Content-type", "text/plain")
        self.end_headers()
        self.wfile.write(b"Data received!")


if __name__ == "__main__":
    server = HTTPServer(("0.0.0.0", 8000), Handler)
    print("🚀 Server running on http://0.0.0.0:8000")
    server.serve_forever()
