import sqlite3

conn = sqlite3.connect('questionsdb.db')
cur = conn.execute('select * from questions')
res = [dict(ID=row[0], query_statement=row[2]) for row in cur.fetchall()]
conn.close()

for i in range(0, len(res)):
    print(res[i])