# LogParser
Parser of logs with query language

Парсер логов.
Парсер просматривает определенную директорию, ищет в ней файлы *.log и выполняет запросы к логам. 

Лог файл имеет следующий формат:
ip username date event status

Где:
ip - ip адрес с которого пользователь произвел событие.
user - имя пользователя (одно или несколько слов разделенные пробелами).
date - дата события в формате day.month.year hour:minute:second.
event - одно из событий:
LOGIN - пользователь залогинился,
DOWNLOAD_PLUGIN - пользователь скачал плагин,
WRITE_MESSAGE - пользователь отправил сообщение,
SOLVE_TASK - пользователь попытался решить задачу,
DONE_TASK - пользователь решил задачу.
Для событий SOLVE_TASK и DONE_TASK существует дополнительный параметр,
который указывается через пробел, это номер задачи.
status - статус:
OK - событие выполнилось успешно,
FAILED - событие не выполнилось,
ERROR - произошла ошибка.

Пример строки из лог файла:
"146.34.15.5 Eduard Petrovich Morozko 05.01.2021 20:22:55 DONE_TASK 48 FAILED".
Записи внутри лог файла не обязательно упорядочены по дате, события могли произойти и быть записаны в лог в разной последовательности.
Все параметры разделены табуляцией ("\t").

В программе реализовано множество методов фильтрации логов по разным критериям.
Так же реализован свой язык запросов, который позволяет строить разные запросы для поиска и фильтрации запросов. 
Язык запросов позволяет формировать запросы с дополнительным параметров вида: 
Общий формат запроса:
get field1 for field2 = "value1" and date between "after" and "before"
Дополнительным параметром может быть только интервал дат, который нас интересует.
поля field1 и field2 может быть одно из  ip, user, date, event или status;
так же возможны сокращенные запросы вида:
get field1 for field2 = "value1"
и
get field1
которые выдают множество всех полей при условии или просто множество всех полей в логах.
Программа написана по мотивам курса JavaRush.
