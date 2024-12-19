package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message;

import lombok.Getter;

@Getter
public class ControllerConstant {
   public static final String  INVITE_ORGANIZATION_TITLE = "Invite Organization";
   public static final String  INVITE_ORGANIZATION_DESCRIPTION = "To invite an organization, kindly provide the industry name,organization name, email address, website address,rc number,tin and phone number ";
   public static final String  REFERENCE_DATA_TITLE = "Organization's Reference data";
   public static final String  REFERENCE_DATA_DESCRIPTION = "Organization's Reference data including service offering and industry type";
   public static final String  DEACTIVATE_ORGANIZATION_TITLE = "Deactivate Organization";
   public static final String  REACTIVATE_ORGANIZATION_TITLE = "Reactivate Organization";
   public static final String  DEACTIVATE_ORGANIZATION_DESCRIPTION = "This endpoint will be used to DEACTIVATE an organization and all its organizations employees";
   public static final String  REACTIVATE_ORGANIZATION_DESCRIPTION = "This endpoint will be used to REACTIVATE an organization and all its organizations its previously deactivated employees";
   public static final String  LOAN_PRODUCT_CREATION = "create loan product";
   public static final String  LOAN_PRODUCT_UPDATE = "update loan product";
   public static final String  VIEW_LOAN_PRODUCT_DETAILS = "View loan product details by id.";
   public static final String  VIEW_LOAN_PRODUCT_DETAILS_DESCRIPTION = "This endpoint is used to view the details of a loan product with unique id";
   public static final String  LOAN_PRODUCT_CREATION_DESCRIPTION = "To create a loan product with unique name";
   public static final String  LOAN_PRODUCT_UPDATE_DESCRIPTION = "To update a loan product with unique name. The id of the loan product must be provided";
   public static final String  LOAN_PRODUCT_VIEW_ALL_DESCRIPTION = "Fetch all loan product in the application using pagination. I.e page number and size.";
   public static final String  LOAN_PRODUCT_VIEW_ALL = "View all loan product";
   public static final String  LOAN_CONTROLLER = "Loan Controller";
   public static final String  LOAN_CONTROLLER_DESCRIPTION = "Manage loans and loan product on the platform";
   public static final String  START_LOAN_DESCRIPTION = "At this point the loan starts. Requirement is the loanee id and the loan offer (id) given to the loanee";
   public static final String  START_LOAN = "Start loan";

   public static final String LOAN_REFERRAL_STATUS_TYPE = "(?i)^(ACCEPTED|DECLINED)$" ;
   public static final String LOAN_REFERRAL_ID_IS_REQUIRED = "Loan referral ID is required";
}
