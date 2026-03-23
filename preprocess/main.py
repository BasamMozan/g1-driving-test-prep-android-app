import json
import base64
import sqlite3
import random
from PIL import Image

directory = "./g1test/public/"
f = open(directory + 'questions.json')
data = json.load(f)

def getCorrectAnswer(question):
    if(question["answer"] == "one"):
        return "a"
    if(question["answer"] == "two"):
        return "b"
    if(question["answer"] == "three"):
        return "c"
    if(question["answer"] == "four"):
        return "d"

def getBase64String(question):
    filePath = directory + question['image']
    # foo = Image.open(filePath)
    # print(foo.size)

    img_file = open(filePath, "rb")
    return base64.b64encode(img_file.read()).decode('utf-8')

def getQuery(question, i):
    stmnt = "What is the given symbol?"
    if(question['question'] != ""):
        stmnt = question['question']
        
    stmnt = "<p><b>Question: " + str(i + 1) + "</b><p><p>" + stmnt + "</p>"

    if(question['image'] != ""):
        stmnt += "<p style=\"text-align:center;\"><img width=\"200\" height=\"200\" src=\"data:image/png;base64," + getBase64String(question) + "\"/></p>"

    stmnt += "<br><br>"
    stmnt += "<div> <table> <tbody>"
    stmnt += "<tr> <td> <div></div> </td> <td> <div> A. " + question['one'] + "</div> </td> </tr>"
    stmnt += "<tr> <td> <div></div> </td> <td> <div> B. " + question['two'] + "</div> </td> </tr>"
    stmnt += "<tr> <td> <div></div> </td> <td> <div> C. " + question['three'] + "</div> </td> </tr>"
    stmnt += "<tr> <td> <div></div> </td> <td> <div> D. " + question['four'] + "</div> </td> </tr>"
    stmnt += "</tbody> </table> </div>"
    stmnt += "<br><br>"
    return stmnt

    
def getQueryStatement(question, i):
    stmnt = "What is the given symbol?"
    if(question['question'] != ""):
        stmnt = question['question']
    return stmnt


def getTopic(question):
    if(question['image'] == ""):
        return "Rules"
    return "Symbols"


def createDatabase():

    conn = sqlite3.connect('questionsdb.db')
    try:
        conn.execute('''CREATE TABLE questions
            (ID INT NOT NULL PRIMARY KEY,
            query TEXT NOT NULL,
            statement TEXT NOT NULL,
            solution TEXT NOT NULL,
            correct TEXT,
            topic TEXT,
            notes TEXT,
            marked TEXT,
            time_txt TEXT,
            flagged INT);''')
        print("Table created successfully")
    except:
        pass
    conn.close()

def writeToDatabase(record):
    conn = sqlite3.connect('questionsdb.db')
    conn.execute("INSERT INTO questions (ID, query, statement, solution, correct, topic, flagged) VALUES (?,?,?,?,?,?,?)", [record["ID"], record["query"], record["statement"],record["solution"], record["correct"], record["topic"], record["flagged"]])
    conn.commit()
    conn.close()



createDatabase()

shuffledQuestions = data['questions']
random.shuffle(shuffledQuestions)


for i in range (0, len(shuffledQuestions)):
    question = shuffledQuestions[i]

    record = {}
    record['ID'] = i + 1
    record['query'] = getQuery(question, i)
    record['statement'] = getQueryStatement(question, i)
    record['solution'] = ""
    record['correct'] = getCorrectAnswer(question)
    record['topic'] = getTopic(question)
    record['notes'] = ""
    record['marked'] = ""
    record['time_txt'] = ""
    record['flagged'] = 0

    # print(record['query'])
    writeToDatabase(record)


f.close()