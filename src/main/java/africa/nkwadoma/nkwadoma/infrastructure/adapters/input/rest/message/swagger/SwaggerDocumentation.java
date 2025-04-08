package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.swagger;

public class SwaggerDocumentation {
    public static final String INVEST_IN_VEHICLE_SUMMARY = "Invest in a vehicle";
    public static final String INVEST_IN_VEHICLE_DESCRIPTION = """
            Allows a financier to invest in a specified vehicle. This action requires the FINANCIER role. \
            The API expects the following request payload:\s
             {\s
            "amountToInvest": "10000",
             "investmentVehicleId": "investmentVehicleId"
             } \
            amountToInvest represents the amount the financier wishes to invest (e.g., 10000), and investmentVehicleId is the unique identifier of the investment vehicle to be funded.""";
}
