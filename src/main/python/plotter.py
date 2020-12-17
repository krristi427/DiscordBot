import matplotlib.pyplot as plt
import sys

if __name__ == '__main__':
    bar_names = []
    bar_heights = []
    textfile = open("src/main/resources/misc/data.txt", "r")

    for line in textfile:
        bar_name, bar_height = line.split()
        bar_names.append(bar_name)
        bar_heights.append(bar_height)

    if sys.argv[1] == "bar":
        plt.bar(bar_names, bar_heights)
        #print("----------> BAR")

    if sys.argv[1] == "pie":
        plt.pie(bar_heights, labels=bar_names, shadow=True, startangle=90)
        #print("----------> PIE")

    if sys.argv[1] == "raw":
        #print("----------> RAW")
        for i in range(len(bar_names)):
            print(bar_names[i]+": "+bar_heights[i])

    plt.savefig('src/main/resources/misc/dataoutput.png')
