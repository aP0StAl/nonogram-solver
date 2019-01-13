package com.apostal.nonogramsolver.util;

public interface ConstTextRU {
    interface Titles {
        String MAIN = "Японский кроссворд";
        String FILE_OPEN = "Открыть файл";
        String IMAGE_PROCESSING = "Обработка фото";
        String ALERT = "Ошибка";
    }

    interface Labels {
        String SOLVE = "Решить";
        String LOAD_PHOTO = "Загрузить фото";
        String PREV_IMAGE = "<- Назад";
        String NEXT_IMAGE = "Далее ->";
        String FINISH = "Готово";
    }

    interface Messages {
        String WINDOWS_STYLE_LOADING_ERROR = "Произошла ошибка при загрузке окна";
        String WRONG_IMAGE = "Не удалось загрузить изображение";
        String LOADING_NN_MODEL_ERROR = "Ошибка загрузки нейронной сети";
        String INCORRECT_INPUT_DATA_ERROR = "Входные данные некорректны";
        String SOLVING_DONE_INFO = "Кроссворд решен";
        String IMAGE_PROCESSING_DONE_INFO = "Распознавание завершено";
    }
}
