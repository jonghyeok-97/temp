package airdnb.be.domain.stay.service.response;

import airdnb.be.domain.stay.entity.Stay;
import airdnb.be.domain.stay.entity.Image;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
import org.locationtech.jts.geom.Point;

public record StayResponse(

        Long stayId,
        Long memberId,
        String title,
        String description,
        LocalTime checkInTime,
        LocalTime checkOutTime,
        BigDecimal feePerNight,
        Integer guestCount,
        Double longitude,
        Double latitude,
        List<String> imageUrls
) {

    public static StayResponse from(Stay stay) {
        Point point = stay.getPoint();
        return new StayResponse(
                stay.getStayId(),
                stay.getMemberId(),
                stay.getTitle(),
                stay.getDescription(),
                stay.getCheckInTime(),
                stay.getCheckOutTime(),
                stay.getFeePerNight(),
                stay.getGuestCount(),
                point.getX(),
                point.getY(),
                getURLs(stay.getImages())
        );
    }

    private static List<String> getURLs(List<Image> images) {
        return images.stream()
                .map(Image::getUrl)
                .collect(Collectors.toList());
    }
}