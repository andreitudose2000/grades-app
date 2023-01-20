# Aplicație pentru gestionarea notelor

## Descriere

Aplicația permite unui student să își țină evidența notelor de la facultate și să calculeaze în timp real numărul de credite, punctele credit și mediile generale (aritmetică și ponderată) pe semestru, pe an și pe întregul program de studiu.
  

Utilizatorului i se pune la dispoziție un REST API prin care poate gestiona:

- programele de studiu pe care le urmează
    
- pentru fiecare program de studiu, anii de studiu

- pentru fiecare program de studiu, tipurile de cursuri (ex: opțional, facultativ etc.)
    
- pentru fiecare an de studiu, semestrele (sau modulele, în cazul în care sistemul este modular)
    
- în fiecare semestru, cursurile pe care le urmează, inclusiv cele care nu contribuie la media generală.

Odată încărcate aceste detalii, utilizatorul poate afla media generală și numărul de credite obținute la nivel de:
- semestru
- an de studiu
- program de studiu.


## Entități

#### Student
- id (int)
- firstName (string)
- lastName (string)
- email (string, unique)

#### Programme
- id (int)
- name (int)
- student_id (int, FK către Student) 

*unique(student_id, name)*
*Programmes M-1 Student*

#### YearOfStudy
- id (int)
- number (int)
- calendarYearOfStart (int)
- calendarYearOfEnd (int)
- programme_id (int, FK către Programme)

*unique(programme_id, number)*
*YearsOfStudy M-1 Programme*

#### CourseType
- id (int)
- name (string)
- isConsideredForGradeAverage (bool)
- programme_id (int, FK către Programme)

*unique(programme_id, name)*
*CourseTypes M-1 Programme*

#### Semester
- id (int)
- number (int)
- year_of_study_id (int, FK către YearOfStudy)

*unique(year_of_study_id, number)*
*Semesters M-1 YearOfStudy*

#### Course
- id (int)
- name (string)
- numberOfCredits (int, > 0)
- grade (int, > 0, < 10)
- course_type_id (int, FK către CourseType)
- semester_id (int, FK către Semester)



## Funcționalități



### Administrare studenți, programe de studiu, ani de studiu, semestre, tipuri de cursuri, cursuri și notele de la cursuri. 

REST API care expune metodele CRUD pentru fiecare dintre aceste entități.

#### Create 
Tipuri de răspunsuri:
- 201 Created - resursa a fost creată
- 404 Not Found - resursa nu a fost găsită
- 409 Conflict - încălcarea unei reguli de unicitate

#### Read
Tipuri de răspunsuri:
- 200 OK - resursa a fost returnată 
- 404 Not Found - resursa nu a fost găsită

#### Update
Tipuri de răspunsuri:
- 204 No Content - resursa a fost actualizată
- 404 Not Found - resursa nu a fost găsită
- 409 Conflict - încălcarea unei reguli de unicitate

#### Delete
O resursă nu poate fi ștearsă dacă altele depind de ea. De exemplu, un semestru nu poate fi șters dacă sunt cursuri asignate lui.

Tipuri de răspunsuri:
- 204 No Content - resursa a fost ștearsă
- 404 Not Found - resursa nu a fost găsită
- 409 Conflict - resursa are alte resurse dependente de ea


### Calcularea mediei generale și a numărului de credite

#### Pe semestru
GET `/students/{studentId}/programmes/{programmeId}/years-of-study/{yearOfStudyId}/semesters/{id}/average`
GET `/students/{studentId}/programmes/{programmeId}/years-of-study/{yearOfStudyId}/semesters/{id}/credits`

#### Pe an
GET `/students/{studentId}/programmes/{programmeId}/years-of-study/{id}/average`
GET `/students/{studentId}/programmes/{programmeId}/years-of-study/{id}/credits`

#### Pe facultate
GET `/students/{studentId}/programmes/{programmeId}/{id}/average`
GET `/students/{studentId}/programmes/{programmeId}/{id}/credits`
