"""
utils.py - Utility functions for database and common operations
"""

from sqlalchemy import create_engine,text
from sqlalchemy.orm import sessionmaker
from config import settings
from models import Base


# Create database engine and session factory
engine = create_engine(
    settings.DATABASE_URL,
    echo=False,  # Set to True for SQL debugging
    pool_pre_ping=True,  # Verify connections before using
    pool_recycle=3600,  # Recycle connections every hour
    connect_args={"charset": "utf8mb4"}  # Ensure UTF-8 support
)

SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)


def init_db():
    """Initialize database - create all tables"""
    Base.metadata.create_all(bind=engine)
    print("✅ Database tables created/verified")


def get_db():
    """Dependency injection for database session"""
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()


def test_db_connection():
    """Test database connection"""
    try:
        db = SessionLocal()
        # 2. WRAP THE STRING IN text()
        db.execute(text("SELECT 1")) 
        db.close()
        print("✅ Database connection successful")
        return True
    except Exception as e:
        print(f"❌ Database connection failed: {str(e)}")
        return False
