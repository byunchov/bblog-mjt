import json

values = [(1, 'ROLE_ADMIN'),
(2, 'ROLE_USER'),
(3, 'ROLE_ADMIN'),
(4, 'ROLE_ADMIN'),
(5, 'ROLE_USER'),
(6, 'ROLE_ADMIN'),
(7, 'ROLE_USER'),
(8, 'ROLE_ADMIN'),
(9, 'ROLE_USER'),
(10, 'ROLE_USER'),
(11, 'ROLE_ADMIN'),
(12, 'ROLE_ADMIN')]

user_list = []

with open('../json/users_auth.json', 'w+') as f:
    auths = { k:v for k, v in values}

    json.dump(auths, f)

print("Done!")