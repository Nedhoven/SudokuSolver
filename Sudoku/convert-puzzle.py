import re
import sys
OFFSET_M1 = ord('A') - 10
OFFSET_ZERO = ord('A') - 10

def get_num(s):
    if sys.argv[3] == 'minus1':
       return get_num_format_minus1(s)
    else:
        return get_num_format_zero(s)

def get_num_format_minus1(s):
    if s == '':
        return ''
    if s.isnumeric():
        return s
    if s == '-1':
        return '-1'
    else:
        return str(ord(s) - OFFSET_M1)


def get_num_format_zero(s):
    if s == '':
        return ''
    if s.isnumeric():
        return str(int(s) - 1)
    elif s == 'A':
        return '9'
    else:
        return str(ord(s) - OFFSET_ZERO - 1)

def get_row(line):
    row = re.sub('\s+', ' ', line).split(' ')
    for i in range(len(row)):
        row[i] = get_num(row[i])
    return row


with open(sys.argv[1], 'r') as inf:
    with open(sys.argv[2], 'w') as outf:
        board = [' '.join(get_row(l.strip())) for l in inf.read().split('\n')]
        outf.write(re.sub('\s+', ' ', ' '.join(board)));
        #outf.write(' '.join([l.strip() for l in inf.read().split('\n')]))
