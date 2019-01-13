## Nonogram solver

Это программа написанная на Java, которая позволяет решать японские кроссворды.
Есть возможность загружать кроссворд по фото.
Можно редактировать кроссворд в UI приложения.
В ближайшее время появится возможность загрузки кроссвордов из текстового файла и с сайта nonogram.com

Алгоритм решения имеет хорошие показатели скорости: ~40-80ms на огромных кроссвордах 100x100+, небольшие кроссворды решаются меньше, чем за 1ms

Распознавание кроссворда по фото происходит с помощью OpenCV и deeplearning4j.
В файле NetworkTrainer реализуется LaNet model для обучения модели распознавания символов
В GenerateDataset реализуется генерация данных для обучения

### Обработка фото

Необработанное изображение:

![enter image description here](https://raw.githubusercontent.com/ap0stal/nonogram-solver/master/Screenshot_1.png)

blurimg:

![enter image description here](https://raw.githubusercontent.com/ap0stal/nonogram-solver/master/Screenshot_3.png)

После обработки шумов и инвертирования цветов:

![enter image description here](https://raw.githubusercontent.com/ap0stal/nonogram-solver/master/Screenshot_4.png)

Выделение области с кроссвордом:

![enter image description here](https://raw.githubusercontent.com/ap0stal/nonogram-solver/master/Screenshot_5.png)

Поиск границ:

![enter image description here](https://raw.githubusercontent.com/ap0stal/nonogram-solver/master/Screenshot_6.png)

Поиск пересечений:

![enter image description here](https://raw.githubusercontent.com/ap0stal/nonogram-solver/master/Screenshot_7.png)

Распознавание ячеек:

![enter image description here](https://raw.githubusercontent.com/ap0stal/nonogram-solver/master/Screenshot_8.png)

Решение кроссворда:

![enter image description here](https://raw.githubusercontent.com/ap0stal/nonogram-solver/master/Screenshot_10.png)
