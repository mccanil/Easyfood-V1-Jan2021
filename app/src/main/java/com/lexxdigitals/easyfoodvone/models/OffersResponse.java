package com.lexxdigitals.easyfoodvone.models;

import java.util.List;

public class OffersResponse {
    boolean success;
    String message;
    Data data;


    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Data getData() {
        return data;
    }

    @Override
    public String toString() {
        return "OffersResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data {
        String total_running_offers;
        String total_expired_offers;
        String total_flat_discount_offers;
        String total_percentage_discount_offers;
        String total_combo_discount_offers;
        List<Offers> offers;


        public String getTotal_running_offers() {
            return total_running_offers;
        }

        public void setTotal_running_offers(String total_running_offers) {
            this.total_running_offers = total_running_offers;
        }

        public String getTotal_expired_offers() {
            return total_expired_offers;
        }

        public void setTotal_expired_offers(String total_expired_offers) {
            this.total_expired_offers = total_expired_offers;
        }

        public String getTotal_flat_discount_offers() {
            return total_flat_discount_offers;
        }

        public void setTotal_flat_discount_offers(String total_flat_discount_offers) {
            this.total_flat_discount_offers = total_flat_discount_offers;
        }

        public String getTotal_percentage_discount_offers() {
            return total_percentage_discount_offers;
        }

        public void setTotal_percentage_discount_offers(String total_percentage_discount_offers) {
            this.total_percentage_discount_offers = total_percentage_discount_offers;
        }

        public String getTotal_combo_discount_offers() {
            return total_combo_discount_offers;
        }

        public void setTotal_combo_discount_offers(String total_combo_discount_offers) {
            this.total_combo_discount_offers = total_combo_discount_offers;
        }

        public List<Offers> getOffers() {
            return offers;
        }

        public void setOffers(List<Offers> offers) {
            this.offers = offers;
        }

        @Override
        public String toString() {
            return "OrdersDetailsTop{" +
                    "total_running_offers='" + total_running_offers + '\'' +
                    ", total_expired_offers='" + total_expired_offers + '\'' +
                    ", total_flat_discount_offers='" + total_flat_discount_offers + '\'' +
                    ", total_percentage_discount_offers='" + total_percentage_discount_offers + '\'' +
                    ", total_combo_discount_offers='" + total_combo_discount_offers + '\'' +
                    ", offers=" + offers +
                    '}';
        }

        public class Offers {
            String id;
            String offer_title;
            String offer_type;
            String start_date;
            String end_date;
            String days_available;


            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getOffer_title() {
                return offer_title;
            }

            public void setOffer_title(String offer_title) {
                this.offer_title = offer_title;
            }

            public String getOffer_type() {
                return offer_type;
            }

            public void setOffer_type(String offer_type) {
                this.offer_type = offer_type;
            }

            public String getStart_date() {
                return start_date;
            }

            public void setStart_date(String start_date) {
                this.start_date = start_date;
            }

            public String getEnd_date() {
                return end_date;
            }

            public void setEnd_date(String end_date) {
                this.end_date = end_date;
            }

            public String getDays_available() {
                return days_available;
            }

            public void setDays_available(String days_available) {
                this.days_available = days_available;
            }

            @Override
            public String toString() {
                return "Offers{" +
                        "id='" + id + '\'' +
                        ", offer_title='" + offer_title + '\'' +
                        ", offer_type='" + offer_type + '\'' +
                        ", start_date='" + start_date + '\'' +
                        ", end_date='" + end_date + '\'' +
                        ", days_available='" + days_available + '\'' +
                        '}';
            }
        }
    }

}
