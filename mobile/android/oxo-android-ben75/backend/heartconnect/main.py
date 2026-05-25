from fastapi import FastAPI, HTTPException
from heartconnect.models import UserProfile

app = FastAPI(title="HeartConnect")

# In-memory store of mock dating profiles with exact coordinates
# These coordinates are fetched by the Android app, which then calculates
# Haversine distances client-side and broadcasts them via implicit intents.
PROFILES = [
    UserProfile(
        id="user_001",
        name="Sarah",
        age=26,
        bio="Coffee lover & weekend hiker. Let's grab a latte!",
        latitude=40.7489,
        longitude=-73.9680,
    ),
    UserProfile(
        id="user_002",
        name="Mike",
        age=29,
        bio="Photography enthusiast. Always chasing golden hour.",
        latitude=40.7614,
        longitude=-73.9776,
    ),
    UserProfile(
        id="user_003",
        name="Jessica",
        age=24,
        bio="Yoga instructor. Looking for positive vibes only.",
        latitude=40.7505,
        longitude=-73.9934,
    ),
    UserProfile(
        id="user_004",
        name="David",
        age=31,
        bio="Foodie exploring NYC's best pizza spots.",
        latitude=40.7656,
        longitude=-73.9782,
    ),
    UserProfile(
        id="user_005",
        name="Emma",
        age=27,
        bio="Art gallery curator. Let's visit a museum together.",
        latitude=40.7398,
        longitude=-73.9847,
    ),
]


@app.get("/profiles", response_model=list[UserProfile])
async def get_profiles() -> list[UserProfile]:
    """Return all nearby match profiles with exact coordinates.

    The Android app fetches these coordinates, calculates Haversine distances
    client-side, and then broadcasts precise distances via implicit intents.
    This creates the trilateration vulnerability.
    """
    return PROFILES


@app.get("/profiles/{profile_id}", response_model=UserProfile)
async def get_profile(profile_id: str) -> UserProfile:
    """Return a single profile by ID."""
    for profile in PROFILES:
        if profile.id == profile_id:
            return profile
    raise HTTPException(status_code=404, detail="Profile not found")
