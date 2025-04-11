package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.swagger;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

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
    public static final String VIEW_INVESTMENT_DETAILS = "View investment details";
    public static final String VIEW_INVESTMENT_DETAILS_DESCRIPTION = """
            Allows a Financier to check their investment details.
            This action requires the FINANCIER role.
            The authenticated user's ID is extracted from the JWT token.
        """;
    public static final String VIEW_INVESTMENT_DETAILS_OF_FINANCIER = "View investment details of financier";


    public static final String VIEW_INVESTMENT_DETAILS_OF_FINANCIER_DESCRIPTION = """
        Allows a Portfolio Manager to check the investment details of a particular financier. 
        This action requires the PORTFOLIO_MANAGER role.
        The API expects the following request payload: 
        {
            "financierId": "UUID"
        }
        financierId is the unique number given to the financier.""";


    public static final String VIEW_DETAILS = "View detail of a financier";
    public static final String VIEW_DETAILS_DESCRIPTION = """
        Retrieves detailed information about a financier using their ID.
        Accessible to users with the roles: PORTFOLIO_MANAGER or FINANCIER.
        If a financier ID is provided, the system fetches details on behalf of the portfolio manager.
        If no ID is provided, it retrieves the details for the currently authenticated financier.
        """;
    
}
