import matplotlib.pyplot as plt
if __name__ == '__main__':
    bar_names = []
    bar_heights = []

    textfile = open("../misc/data.txt", "r")

    for line in textfile:
        bar_name, bar_height = line.split()
        bar_names.append(bar_name)
        bar_heights.append(bar_height)
    plt.bar(bar_names, bar_heights)
    plt.savefig('../misc/dataoutput.png')
