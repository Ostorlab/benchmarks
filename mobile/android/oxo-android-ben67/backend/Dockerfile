FROM python:3.11-slim
                     
WORKDIR /app         
                     
COPY log_server.py . 
COPY requirements.txt .                                                                                                                                                                 
                     
EXPOSE 8000          
                     
CMD ["python", "server.py"]