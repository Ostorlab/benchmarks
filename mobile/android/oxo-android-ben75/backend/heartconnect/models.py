from pydantic import BaseModel


class UserProfile(BaseModel):
    id: str
    name: str
    age: int
    bio: str
    latitude: float
    longitude: float
