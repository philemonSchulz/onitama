# Onitama Project

This repository contains a running program for Onitama, developed as part of the bachelor's thesis "Exploring the applicability of agent-based AI to the game Onitama".

## Features

- Play Onitama locally against AI models
- Start a client and play against other players

## Overview

The Onitama program provides a terminal interface for playing the board game Onitama. It offers two main modes of play:

1. **Local AI Play**: Challenge various AI models implemented within the client.
   - **Player vs. AI**: Test your skills against different AI opponents.
   - **AI vs. AI**: Observe matches between different AI models for analysis
3. **Online Play**: Connect to the Onitama API for multiplayer games or matches against server-side AI.

## Requirements

To run the project, you need to install a Java 22 JDK as well as Maven. Afterward, a Maven install has to be executed to install the required plugins.

## Getting Started
To start playing, you need to execute the Client.java file.

## Connecting to Onitama API

To play online or against server-side AI, you need to run the Api.java main method.

## AI Models

There are six AI agents to choose from:
1. A random AI, making only random moves.
2. A random AI with priority for capturing and winning moves. This AI will play random unless it has the chance to capture an opponent's piece or make a move that leads to a direct win.
3. An Heuristic agent, using hardcoded heuristics for move selection.
4. An MCTS agent, using UCT Monte Carlo Tree Search and the move prioritization of agent 2 in its simulation phase.
5. An MCTS RAVE agent, using UCT Monte Carlo Tree Search together with Rapid Action Value estimation and the move prioritization of agent 2 in its simulation phase.
6. AN MCTS Heurisitc agent, using UCT Monte Carlo Tree Search together with a heavy playout using the heuristics of agent 3.

More about the agents can be found in the bachelor's thesis.

## Bachelors Thesis

The corresponding bachelor's thesis is uploaded to this repository.
