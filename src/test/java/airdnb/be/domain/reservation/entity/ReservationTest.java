package airdnb.be.domain.reservation.entity;

import static org.assertj.core.api.Assertions.assertThat;

import airdnb.be.domain.reservation.embedded.ReservationStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ReservationTest {

    @DisplayName("체크인, 체크아웃으로 예약될 날짜를 생성한다.")
    @Test
    void createReservationDate() {
        // given
        Reservation reservation = new Reservation(
                1L,
                1L,
                LocalDateTime.of(2024, 9, 1, 15, 0),
                LocalDateTime.of(2024, 9, 5, 11, 0),
                3,
                new BigDecimal(30000)
        );

        // when
        List<ReservationDate> reservationDates = reservation.createReservationDate();

        // then
        Assertions.assertThat(reservationDates)
                .extracting("reservationDate")
                .containsExactlyInAnyOrderElementsOf(List.of(
                        LocalDate.of(2024, 9, 1),
                        LocalDate.of(2024, 9, 2),
                        LocalDate.of(2024, 9, 3),
                        LocalDate.of(2024, 9, 4)
                ));
    }

    @DisplayName("예약을 생성하면 초기 상태는 '예약됨' 이다.")
    @Test
    void initStatusReservedWhenReservationDateCreate() {
        // given
        Reservation reservation = new Reservation(
                1L,
                1L,
                LocalDateTime.of(2024, 9, 1, 15, 0),
                LocalDateTime.of(2024, 9, 5, 11, 0),
                3,
                new BigDecimal(30000)
        );

        // when
        ReservationStatus status = reservation.getStatus();

        // then
        assertThat(status).isEqualTo(ReservationStatus.RESERVED);
    }
}