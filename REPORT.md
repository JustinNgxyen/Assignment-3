# Project Report

## Challenges I Faced
**Challenge 1: Calling to API**
Problem: A lot of Spotify's APIs are deprecated 
Solution: I did my best and return the artist's top tracks
Learned: Use APIs that are still supported and not deprecated

**Challenge 2: Errors would crash the program**
Problem: Anytime an error appeared, it would crash the program 
Solution: Catch the error and output it to user
Learned: Use try-catch blocks to handle errors, return useful info. to user

## Design Pattern Justifications
**Strategy Pattern:** Use RecommendationStrategy Interface for all recommendation systems, while FeatureBasedStrategy implements its version
**Factory Pattern:** RecommendationEngine creates and manages recommendation strategy based on the user input
**Observer Pattern:** Event-driven interaction between GUI and controller. The View notifies the Controller of the user's actions.

## AI Usage (BE HONEST!)
**Used ChatGPT to create the UI using Swing**
Asked: "Create a simple interface for the music recommender"
Modified: Swing UI 
Verified: Tested by running the program

**Used ClaudeAI to help implement the FeatureBasedStrategy**
Asked: "What is a simple way to return artist's top tracks as a recommendation?"
Modified: How an artist's top tracks are returned using the Spotify artist ID
Verified: FeatureBasedStrategyTest, GetTopTracksbyArtistTest

## Time Spent: ~3-4 weeks
