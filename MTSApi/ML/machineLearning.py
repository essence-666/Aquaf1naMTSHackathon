import whisper
import json
import base64
import os
import pymorphy2

morph = pymorphy2.MorphAnalyzer()


with open("./ML/constants.json", "r", encoding="utf-8") as f:
    data = json.load(f)


category_keywords = {
    category: {morph.parse(word.lower())[0].normal_form for word in words}
    for category, words in data["Categories"].items()
}


def get_category_value(category : str):
    with open("./ML/constants.json", "r", encoding="utf-8") as file:
        data = json.load(file)

    important = data.get("Important", {})

    return important.get(category, "Категория не найдена")




def classify_problem(text : str):
    """Классифицирует проблему по ключевым словам."""
    words = text.split()
    problem_categories = {}

    for word in words:
        normal_form = morph.parse(word.lower())[0].normal_form
        for category, keywords in category_keywords.items():
            if normal_form in keywords:
                if category in problem_categories:
                    problem_categories[category] += 1
                else:
                    problem_categories[category] = 1

    if problem_categories:
        return max(problem_categories, key=problem_categories.get)  # Категория с наибольшим совпадением
    return "Неопределённая проблема"


def get_results(transcription: str):
    """Возвращает результаты классификации."""
    topic = classify_problem(transcription)
    comment = ""
    spam = True if (get_category_value(topic) == "Категория не найдена"
                    or topic == "Неопределённая проблема") else False
    important = True if (get_category_value(topic) == 1) else False

    return topic, comment, spam, important