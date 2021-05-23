# DiscordBot

[![Forks][forks-shield]][forks-url]
[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]

<!-- ABOUT THE PROJECT -->
## About The Project

![Product Name Screen Shot][product-screenshot]

This is a simple Discord Bot built by 2 Students of the University of Bonn.
The Bot can do many things: starting from simple greetings and telling jokes, 
to setting timers, making polls and even joining you on a voice channel. 

### Built With

The Main Libraries and APIs we used for this Application are: 
* [JDA](https://github.com/DV8FromTheWorld/JDA)
* [LavaPlayer](https://github.com/sedmelluq/lavaplayer)
* [Reflections](https://github.com/ronmamo/reflections)
* [Dad jokes](https://rapidapi.com/KegenGuyll/api/dad-jokes/details)


## Getting Started

Here we will explain how to get this Bot up and running.

### Prerequisites

* _Java 8 or later_

  Make sure to have Java 8 or later installed. However, the current version is recommended. 
* _IDE_

    You will need an IDE to be able to start your bot. We would recommend IntelliJ IDEA.
* _Discord Account_ 

    Logically, you will also need a discord account in order to run your bot 

* _API key_
    
    The used API needs a key to be functional. You can subscribe for free: this allows 50 requests per day. 

### Installation

1. Clone the repo
   ```sh
   git clone https://github.com/krristi427/DiscordBot.git your_directory
   ```
   
2. Create a new Application [here](https://discord.com/developers/applications). Copy the token from there
3. Open ```resources/config.properties``` and replace the token there with one of your own. 
4. Replace the contents of the variable jokeApiKey with the key provided from the api.  
5. Run the Application

Bear in mind that the prefix is ```%```. You can change that in the same file, just watch out that the new prefix 
doesn't match the prefix of another bot on your server. 


<!-- CONTRIBUTING -->
## Contributing

Contributions are what make the open source community such an amazing place to be! Any contributions you make are **greatly appreciated**.

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

[forks-shield]: https://img.shields.io/github/forks/krristi427/DiscordBot
[forks-url]: https://github.com/krristi427/DiscordBot/network/members

[stars-shield]: https://img.shields.io/github/stars/krristi427/DiscordBot
[stars-url]: https://github.com/krristi427/DiscordBot/stargazers

[issues-shield]: https://img.shields.io/github/issues/krristi427/DiscordBot
[issues-url]: https://github.com/krristi427/DiscordBot/issues

[product-screenshot]: img/helloWorld.png
