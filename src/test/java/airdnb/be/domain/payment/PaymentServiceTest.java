package airdnb.be.domain.payment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import airdnb.be.IntegrationTestSupport;
import airdnb.be.domain.payment.entity.PaymentTemporary;
import airdnb.be.domain.payment.service.PaymentService;
import airdnb.be.domain.payment.service.request.PaymentConfirmServiceRequest;
import airdnb.be.domain.reservation.ReservationRepository;
import airdnb.be.domain.reservation.entity.Reservation;
import airdnb.be.exception.BusinessException;
import airdnb.be.exception.ErrorCode;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class PaymentServiceTest extends IntegrationTestSupport {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private PaymentTemporaryRepository paymentTemporaryRepository;

    @AfterEach
    void tearDown() {
        reservationRepository.deleteAllInBatch();
        paymentTemporaryRepository.deleteAllInBatch();
    }

    @DisplayName("결제 임시 데이터 저장중 결제하려는 총 금액이 맞지 않으면 예외가 발생한다.")
    @Test
    void addPaymentTemporaryDataIsFailByAmount() {
        // given
        Reservation reservation = new Reservation(
                1L,
                1L,
                LocalDateTime.of(2024, 5, 2, 15, 0),
                LocalDateTime.of(2024, 5, 10, 11, 1),
                3,
                new BigDecimal(30000)
        );
        Reservation saved = reservationRepository.save(reservation);

        // when then
        assertThatThrownBy(() -> paymentService.addPaymentTemporaryData(1L, saved.getReservationId(),
                        "paymentKey", "40000", "orderId"))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode").isEqualTo(ErrorCode.NOT_EQUAL_AMOUNT);
    }

    @DisplayName("결제 임시 데이터를 저장한다")
    @Test
    void addPaymentTemporaryData() {
        // given
        Reservation reservation = new Reservation(
                1L,
                1L,
                LocalDateTime.of(2024, 5, 2, 15, 0),
                LocalDateTime.of(2024, 5, 10, 11, 1),
                3,
                new BigDecimal(30000)
        );
        Reservation saved = reservationRepository.save(reservation);
        System.out.println("saved.hasTotalFee(\"30000\") = " + saved.hasTotalFee("30000"));

        // when
        Long temporaryId = paymentService.addPaymentTemporaryData(1L, saved.getReservationId(),
                "paymentKey", "30000", "orderId");

        // then
        int savedSize = paymentTemporaryRepository.findAll().size();
        assertThat(savedSize).isEqualTo(1);
        assertThat(temporaryId).isNotNull();
    }

    @DisplayName("결제 승인 전에 결제 임시 데이터와 비교한다")
    @Test
    void existsPaymentTemporary() {
        // given
        PaymentTemporary paymentTemporary = new PaymentTemporary(
                1L, 1L, "orderId", "paymentKey", "amount");
        PaymentTemporary saved = paymentTemporaryRepository.save(paymentTemporary);

        PaymentConfirmServiceRequest request = new PaymentConfirmServiceRequest(
                saved.getPaymentTemporaryId(),
                1L,
                1L,
                "paymentKey",
                "amount",
                "orderId"
        );

        // when then
        assertThatCode(() -> paymentService.validateExistingPaymentTemporary(request))
                .doesNotThrowAnyException();
    }

    @DisplayName("결제 승인 전에 결제 임시 데이터가 없으면 예외가 발생한다")
    @Test
    void existsPaymentTemporaryWithFail() {
        // given
        PaymentConfirmServiceRequest request = new PaymentConfirmServiceRequest(
                1L,
                1L,
                1L,
                "paymentKey",
                "amount",
                "orderId"
        );

        // when then
        assertThatThrownBy(() -> paymentService.validateExistingPaymentTemporary(request))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.NOT_EXIST_TEMPORARY_DATA);
    }
}