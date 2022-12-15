import json
import csv

keys = ('id', 'title', 'content', 'user_id', 'created_at', 'updated_at')

user_list = []

post_list = []

with open('posts_content.txt', 'r+') as f:
    reader = csv.reader(f, delimiter='|')
    for line in reader:
        data = list(map(str.strip, line))
        post_list.append(dict(zip(keys, data)))

with open('../json/posts_content.json', 'w+') as f:
    json.dump(post_list, f)

print("Done!")