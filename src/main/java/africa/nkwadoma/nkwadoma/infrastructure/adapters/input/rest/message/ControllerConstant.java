package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message;

import lombok.Getter;

@Getter
public class ControllerConstant {
   public static final String  INVITE_ORGANIZATION_TITLE = "Invite Organization";
   public static final String  INVITE_ORGANIZATION_DESCRIPTION = "To invite an organization, kindly provide the industry name,organization name, email address, website address,rc number,tin and phone number ";
   public static final String  ADD_BANK_DETAIL_DESCRIPTION = "Add and update bank details for transaction purposes";
    public static final String  BANK_DETAIL = "Bank detail";
    public static final String  VIEW_BANK_DETAIL_DESCRIPTION = "To view bank details";
    public static final String  APPROVE_OR_DECLINE_BANK_DETAIL_DESCRIPTION = "To approve or decline bank details by super admin of either, organization, cooperate financier, or meedl ";
   public static final String  REFERENCE_DATA_TITLE = "Organization's Reference data";
   public static final String  REFERENCE_DATA_DESCRIPTION = "Organization's Reference data including service offering and industry type";
   public static final String  DEACTIVATE_ORGANIZATION_TITLE = "Deactivate Organization";
   public static final String  REACTIVATE_ORGANIZATION_TITLE = "Reactivate Organization";
   public static final String  DEACTIVATE_ORGANIZATION_DESCRIPTION = "This endpoint will be used to DEACTIVATE an organization and all its organizations employees";
   public static final String  REACTIVATE_ORGANIZATION_DESCRIPTION = "This endpoint will be used to REACTIVATE an organization and all its organizations its previously deactivated employees";
   public static final String  LOAN_PRODUCT_CREATION = "Create loan product";
   public static final String LOAN_BOOK_USER_DATA_CREATION_VIA_FILE_UPLOAD = "Loan book: Uploading users bio data via file upload.";
   public static final String LOAN_BOOK_REPAYMENT_RECORD_CREATION_VIA_FILE_UPLOAD = "Loan book: Uploading users repayment record via file upload.";
   public static final String  CREATE_DISBURSEMENT_RULE = "Create disbursement rule";
   public static final String  VIEW_DISBURSEMENT_RULE = "View disbursement rule";
   public static final String  LOAN_PRODUCT_UPDATE = "update loan product";
   public static final String  VIEW_LOAN_PRODUCT_DETAILS = "View loan product details by id.";
   public static final String  DELETE_LOAN_PRODUCT_DETAILS = "Delete loan product by id.";
   public static final String  SET_OBLIGOR_LIMIT = "Setting of meedl's obligor loan limits.";
   public static final String  SET_OBLIGOR_LIMIT_DESCRIPTION = "Setting of meedl's general obligor loan limits. This action can only be done by the super admin";
   public static final String  VIEW_LOAN_PRODUCT_DETAILS_DESCRIPTION = "This endpoint is used to view the details of a loan product with unique id";
   public static final String  DELETE_LOAN_PRODUCT_DETAILS_DESCRIPTION = "This endpoint is used to delete a loan product with unique id";
   public static final String  LOAN_PRODUCT_CREATION_DESCRIPTION = "To create a loan product with unique name";
   public static final String LOAN_BOOK_USER_DATA_CREATION_DESCRIPTION = "To create a loan book via file upload of user data. Either an excel file or a csv file upload.";
   public static final String LOAN_BOOK_REPAYMENT_RECORD_CREATION_DESCRIPTION = "To create a loan book via file upload of users repayment records. Either an excel file or a csv file upload.";
   public static final String  LOAN_PRODUCT_UPDATE_DESCRIPTION = "To update a loan product with unique name. The id of the loan product must be provided";
   public static final String  CREATE_DISBURSEMENT_RULE_DESCRIPTION = "To create a disbursement rule with unique name.";
   public static final String  VIEW_DISBURSEMENT_RULE_DESCRIPTION = "To view a disbursement rule detail by unique id.";
   public static final String  LOAN_PRODUCT_VIEW_ALL_DESCRIPTION = "Fetch all loan product in the application using pagination. I.e page number and size.";
   public static final String  LOAN_PRODUCT_VIEW_ALL = "View all loan product";
   public static final String  LOAN_CONTROLLER = "Loan Controller";
   public static final String  LOAN_BOOK_CONTROLLER = "Loan Book Controller";
   public static final String  LOAN_CONTROLLER_DESCRIPTION = "Manage loans and loan product on the platform";
   public static final String  LOAN_BOOK_CONTROLLER_DESCRIPTION = "Manage all loans on the platform. Loanee loan book management.";
   public static final String  START_LOAN_DESCRIPTION = "At this point the loan starts. Requirement is the loanee id and the loan offer (id) given to the loanee";
   public static final String  START_LOAN = "Start loan";


   public static final String RESPONSE_IS_SUCCESSFUL = "Response is successful";
   public static final String LOAN_REFERRAL_STATUS_TYPE = "(?i)^(ACCEPTED|DECLINED)$" ;
   public static final String LOAN_REFERRAL_ID_IS_REQUIRED = "Loan referral ID is required";
}
