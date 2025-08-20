from hanspell import spell_checker


f=open('1.txt','r',encoding='utf8')
txt=f.readlines()
f.close()
new_txt=[]
for i in txt:
    new_txt.append(i.replace('\n',""))
    str_txt=""
for i in new_txt:
    str_txt =str_txt + i + ""

# for x in range(3):
#     ['sent{}'.format(x)]=str_txt[500*x:500*x+499]

for i in range(0, 50):
    globals()['sent{}'.format(i)] = str_txt[500*i:500*i+499]
    # print(globals()['sent{}'.format(i)])
    

    spelled_sent = spell_checker.check(globals()['sent{}'.format(i)])
    # print(spelled_sent)
    checked_sent = spelled_sent.checked
    # print(checked_sent)

    f=open('2.txt','a+',encoding='utf8')
    f.write(checked_sent)
    f.close()










