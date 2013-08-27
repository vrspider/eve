---
layout: default
title: Concepts Introduction
---

# Concepts Introduction {#top}

Eve is a multipurpose, web based agent platform, in which existing web technologies are used to provide an environment in which software agents can be developed. Eve is defined as an agent model and a communication protocol, which can be implemented in many programming languages en runtime infrastructures. This part of the documentation provides an introduction into these generic elements of Eve, with separate sections dedicated to the existing implementations.

This page has the following paragraphs:
- [Agent definition](#agentdefinition)
- [Protocol driven](#protocoldriven)
- [Multi-platform](#multiplatform)
- [Open Source](#opensource)


## "Agent" definition {#agentdefinition}

For a good understanding of Eve, it is important to look at it's concept "Agent". The basic definition of agent is: A software entity that represents existing, external, sometimes abstract, entities. Examples of such entities are: human beings, physical objects, abstract goals, etc. To be able to function, the agent needs to be able to run independently of the entity it represents. This autonomous behavior requires a basic set of features, which Eve provides for its agents. These features are: 
- **time independence**, own scheduling, independent of the represented entity.
- **memory**, the possibility to keep a model of the state of the world
- **communication**, a common language to communicate between agents

Eve provides these features as services to the agents, therefor the implementation of the agent can focus on the domain specific logic and data management.

<img src="/eve/img/eve_agent.png"
  style="margin-top: 30px;width:75%;margin-left:auto;margin-right:auto;display:block" 
  title="Eve agentmodel infograph">

The main reason for providing a separate memory service to the agents is that, in most implementations, Eve agents have a request based lifecycle. The agent is only instantiated to handle a single incoming request and is destroyed again as soon as the request has been handled. Only the externally stored state is kept between requests. The agent identity is formed by the address of its state, not by an in-memory, running instance of some class. 

This model mimics the way modern webservers handle servlets, allowing any servlet to become an Eve agent. Out of the box, a single agent (=single state) can therefor execute requests in parallel, multi-threaded, and (depending on the state-implementation) distributed among multiple servers. This model also allows for easier handling of asynchronous agent designs.

## Protocol driven {#protocoldriven}
	JSON-RPC on top of transport layers

## Multi-platform {#multiplatfrom}
	Transport layers allow various environments
	Servlet, mobile, in browser

## Open source {#opensource}

Eve is an open platform, and it is encouraged to help extending the
platform by providing new libraries with agents, or create implementations in
other programming languages, or improve existing code. 
One of the key issues for the platform to be successful is that it needs to be
accessible, open, and easy to implement for any developer in any development
environment. 

Offering the software as open source is a logic result of the aims for 
openness and collaborative development.
