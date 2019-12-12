import sys


with open(sys.argv[1], 'r') as f:
    st = f.read()
    count = 0
    for s in st.split(' '):
        if s == '' or s == '-1':
            continue
        else:
            count += 1

print(count)
