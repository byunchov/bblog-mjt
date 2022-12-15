import json

keys = ('id', 'first_name', 'last_name', 'username', 'password', 'email')

values = [(1, 'Janine', 'Bellenie', 'jbellenie0', 'nLisIShTfF', 'jbellenie0@cisco.com'),
(2, 'Gavra', 'Dedden', 'gdedden1', 'z3K0ziCg', 'gdedden1@hugedomains.com'),
(3, 'Chuck', 'Tegler', 'ctegler2', 'VFZS3MitjCCU', 'ctegler2@digg.com'),
(4, 'Mitchell', 'Farlambe', 'mfarlambe3', 'uCXMYOOQ', 'mfarlambe3@tinyurl.com'),
(5, 'Mycah', 'Skullet', 'mskullet4', 'ViuFLH1F5AFk', 'mskullet4@slashdot.org'),
(6, 'Shellie', 'Jaukovic', 'sjaukovic5', 'ksaFNuSEgC', 'sjaukovic5@sciencedirect.com'),
(7, 'Rita', 'Piscot', 'rpiscot6', 'dSF577IGQSt', 'rpiscot6@mashable.com'),
(8, 'Ingeborg', 'Radbourn', 'iradbourn7', 'P9AUg5JN', 'iradbourn7@storify.com'),
(9, 'Corrie', 'Morfey', 'cmorfey8', 'tsKtbp8ot8RH', 'cmorfey8@friendfeed.com'),
(10, 'Lewie', 'Shilburne', 'lshilburne9', 'aHck3I6k', 'lshilburne9@123-reg.co.uk'),
(11, 'Finley', 'Frise', 'ffrisea', 'NSZqjHOV', 'ffrisea@ox.ac.uk'),
(12, 'Danyelle', 'Andriuzzi', 'dandriuzzib', 'YN0KAAKV', 'dandriuzzib@360.cn')]

# with open('users_java_map.txt', 'w+') as f:

#     f.write("Arrays.asList(\n")

#     for row in values:
#         user_data = zip(keys, row)

#         f.write("new HashMap<String, String>() {{\n")

#         for item in user_data:
#             f.write(f'     put("{item[0]}", "{item[1]}");\n')

#         f.write("}},\n")

#     f.write(");")

user_list = []

with open('../json/users.json', 'w+') as f:
    for row in values:
        data = list(row)
        data[0] = str(data[0])
        user_list.append(dict(zip(keys, data)))

    json.dump(user_list, f)

print("Done!")