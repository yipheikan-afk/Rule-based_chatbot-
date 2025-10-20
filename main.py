import random
import string
import json

rules = {
    "hello": ["Hello!", "Hi there!", "nice to meet you!"],
    "how are you": ["I'm doing good, thanks!", "All good here!", "I'm ready to chat."],
    "name": ["You can call me Bot.", "I'm your chatbot.", "I'm just Bot."],
    "weather": ["I can't check the weather, but I hope it's nice where you are."],
    "python": ["Python is a great language!", "I like Python too."],
    "thanks": ["You're welcome!", "No problem!", "Glad to help."],
    "bye": ["Goodbye!", "See you later!", "Take care!"]
}

try:
    with open("data_translated.txt", "r") as f:
        extra = json.loads(f.read())
        rules.update(extra)
except:
    pass


def clean_text(text):
    text = text.lower().strip()
    text = text.translate(str.maketrans('', '', string.punctuation))
    return text


def reply(user_text):
    msg = clean_text(user_text)
    for key, responses in rules.items():
        if key in msg:
            return random.choice(responses)
    fallback = [
        "That's interesting.",
        "I'm not sure what to say.",
        "Tell me more.",
        "Let's talk about something else."
    ]
    return random.choice(fallback)


def chat():
    print("Chatbot is ready. Type 'quit' to exit.")
    while True:
        try:
            user_text = input("You: ")
            if user_text.lower() in ["quit", "exit"]:
                print("Bot: Goodbye!")
                break
            print("Bot:", reply(user_text))
        except KeyboardInterrupt:
            print("\nBot: Goodbye!")
            break


if __name__ == "__main__":
    chat()
