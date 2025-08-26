from fastapi import FastAPI
from purplecloud.router import router
from purplecloud.storage import storage_provider

app: FastAPI = FastAPI(title="PurpleCloud")


@app.on_event("startup")
async def startup_event() -> None:
    storage_provider.setup()


app.include_router(router)
