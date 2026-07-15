rootProject.name = "kafka-msa-ecommerce"

include(
    "common:common-events",
    "common:common-kafka-config",
    "services:order-service",
    "services:payment-service",
    "services:inventory-service",
    "services:shipping-service",
    "services:notification-service"
)
