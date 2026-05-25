#!/usr/bin/env python3
"""Entry point to run the HeartConnect backend server."""

import uvicorn

if __name__ == "__main__":
    uvicorn.run("heartconnect.main:app", host="0.0.0.0", port=8000, reload=True)
