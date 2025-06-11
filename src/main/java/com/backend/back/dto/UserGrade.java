package com.backend.back.dto;


public enum UserGrade {
    준회원(1),
    새싹회원(2),
    초급회원(3),
    일반회원(4),
    중급회원(5),
    정회원(6),
    고급회원(7),
    명예회원(8),
    운영진(9),
    관리자(10);

    private final int grade;	// enum 객체 생성시 value 값

    UserGrade(int value) {
        this.grade = value;		// 생성시 value 설정 | 객체 생성시 상수의 () 내부값이 자동으로 설정
    }

    public int getGrade() {		// 저장된 value
        return grade;
    }

    public static UserGrade fromGrade(int grade) {
        for (UserGrade val : UserGrade.values()) {		// 모든 상수(value)를 가져옴
            if (val.getGrade() == grade) {				// 상수의 value가 입력값과 같으면 리턴
                return val;
            }
        }
        throw new IllegalArgumentException("Invalid grade value: " + grade);		// 범위 초과시 에러 메세지
    }
}
