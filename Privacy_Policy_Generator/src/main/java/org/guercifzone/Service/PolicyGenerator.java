package org.guercifzone.Service;

import org.guercifzone.Models.AppInfo;
import org.guercifzone.Models.DataCollection;
import org.guercifzone.Models.Permissions;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PolicyGenerator {

    public String generatePolicy(AppInfo appInfo, Permissions permissions, DataCollection dataCollection) {
        StringBuilder policy = new StringBuilder();
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        // Header and Introduction
        policy.append(generateHeader(appInfo, date));
        policy.append(generateIntroduction(appInfo));

        // Information Collection
        if (dataCollection.collectsAnyData()) {
            policy.append(generateInformationCollection(dataCollection));
        }

        // Standard Sections
        policy.append(generateLogDataSection());

        // Conditional Sections
        if (dataCollection.usesCookies()) {
            policy.append(generateCookiesSection());
        }

        if (dataCollection.usesThirdPartyServices()) {
            policy.append(generateServiceProvidersSection());
        }

        // Standard Footer Sections
        policy.append(generateSecuritySection());
        policy.append(generateLinksToSitesSection());
        policy.append(generateChildrensPrivacySection());
        policy.append(generatePolicyChangesSection(date));
        policy.append(generateContactUsSection(appInfo));

        return policy.toString();
    }

    private String generateHeader(AppInfo appInfo, String date) {
        return "<h1>Privacy Policy for " + escapeHtml(appInfo.getAppName()) + "</h1>\n" +
                "<p><em>Last updated: " + date + "</em></p>\n\n";
    }

    private String generateIntroduction(AppInfo appInfo) {
        return "<h2>Introduction</h2>\n" +
                "<p>" + escapeHtml(appInfo.getDeveloperName()) + " built the " +
                escapeHtml(appInfo.getAppName()) + " app" +
                (appInfo.getAppDescription().isEmpty() ? "" : " (" + escapeHtml(appInfo.getAppDescription()) + ")") +
                ". This SERVICE is provided by " + escapeHtml(appInfo.getDeveloperName()) +
                " at no cost and is intended for use as is.</p>\n" +
                "<p>This page is used to inform visitors regarding our policies with the collection, use, " +
                "and disclosure of Personal Information if anyone decided to use our Service.</p>\n" +
                "<p>If you choose to use our Service, then you agree to the collection and use of " +
                "information in relation to this policy. The Personal Information that we collect is " +
                "used for providing and improving the Service. We will not use or share your information " +
                "with anyone except as described in this Privacy Policy.</p>\n\n";
    }

    private String generateInformationCollection(DataCollection dataCollection) {
        StringBuilder section = new StringBuilder();
        section.append("<h2>Information Collection and Use</h2>\n")
                .append("<p>For a better experience, while using our Service, we may require you to provide us with ")
                .append("certain personally identifiable information. The information that we request will be ")
                .append("retained by us and used as described in this privacy policy.</p>\n");

        if (dataCollection.collectsAnyData()) {
            section.append("<p>The app uses the following data:</p>\n<ul>\n");
            if (dataCollection.collectsEmail()) section.append("<li>Email address</li>\n");
            if (dataCollection.collectsName()) section.append("<li>Name</li>\n");
            if (dataCollection.collectsPhone()) section.append("<li>Phone number</li>\n");
            if (dataCollection.collectsLocation()) section.append("<li>Location data</li>\n");
            if (dataCollection.collectsDeviceInfo()) section.append("<li>Device information (model, OS version, etc.)</li>\n");
            if (dataCollection.collectsUsageData()) section.append("<li>Usage data (how you interact with the app)</li>\n");
            section.append("</ul>\n");
        }

        section.append("\n");
        return section.toString();
    }

    private String generateLogDataSection() {
        return "<h2>Log Data</h2>\n" +
                "<p>We want to inform you that whenever you use our Service, in a case of an error in the app " +
                "we collect data and information (through third-party products) on your phone called Log Data. " +
                "This Log Data may include information such as your device Internet Protocol (\"IP\") address, " +
                "device name, operating system version, the configuration of the app when utilizing our Service, " +
                "the time and date of your use of the Service, and other statistics.</p>\n\n";
    }

    private String generateCookiesSection() {
        return "<h2>Cookies</h2>\n" +
                "<p>Cookies are files with a small amount of data that are commonly used as anonymous unique " +
                "identifiers. These are sent to your browser from the websites that you visit and are stored on " +
                "your device's internal memory.</p>\n" +
                "<p>This Service does not use these \"cookies\" explicitly. However, the app may use third-party " +
                "code and libraries that use \"cookies\" to collect information and improve their services. You " +
                "have the option to either accept or refuse these cookies and know when a cookie is being sent " +
                "to your device. If you choose to refuse our cookies, you may not be able to use some portions " +
                "of this Service.</p>\n\n";
    }

    private String generateServiceProvidersSection() {
        return "<h2>Service Providers</h2>\n" +
                "<p>We may employ third-party companies and individuals due to the following reasons:</p>\n" +
                "<ul>\n" +
                "<li>To facilitate our Service;</li>\n" +
                "<li>To provide the Service on our behalf;</li>\n" +
                "<li>To perform Service-related services; or</li>\n" +
                "<li>To assist us in analyzing how our Service is used.</li>\n" +
                "</ul>\n" +
                "<p>We want to inform users of this Service that these third parties have access to their " +
                "Personal Information. The reason is to perform the tasks assigned to them on our behalf. " +
                "However, they are obligated not to disclose or use the information for any other purpose.</p>\n\n";
    }

    private String generateSecuritySection() {
        return "<h2>Security</h2>\n" +
                "<p>We value your trust in providing us your Personal Information, thus we are striving to " +
                "use commercially acceptable means of protecting it. But remember that no method of " +
                "transmission over the internet, or method of electronic storage is 100% secure and " +
                "reliable, and we cannot guarantee its absolute security.</p>\n\n";
    }

    private String generateLinksToSitesSection() {
        return "<h2>Links to Other Sites</h2>\n" +
                "<p>This Service may contain links to other sites. If you click on a third-party link, " +
                "you will be directed to that site. Note that these external sites are not operated by us. " +
                "Therefore, we strongly advise you to review the Privacy Policy of these websites. We have " +
                "no control over and assume no responsibility for the content, privacy policies, or " +
                "practices of any third-party sites or services.</p>\n\n";
    }

    private String generateChildrensPrivacySection() {
        return "<h2>Children's Privacy</h2>\n" +
                "<p>These Services do not address anyone under the age of 13. We do not knowingly collect " +
                "personally identifiable information from children under 13. In the case we discover that " +
                "a child under 13 has provided us with personal information, we immediately delete this " +
                "from our servers. If you are a parent or guardian and you are aware that your child has " +
                "provided us with personal information, please contact us so that we will be able to do " +
                "the necessary actions.</p>\n\n";
    }

    private String generatePolicyChangesSection(String date) {
        return "<h2>Changes to This Privacy Policy</h2>\n" +
                "<p>We may update our Privacy Policy from time to time. Thus, you are advised to review " +
                "this page periodically for any changes. We will notify you of any changes by posting the " +
                "new Privacy Policy on this page.</p>\n" +
                "<p>This policy is effective as of " + date + "</p>\n\n";
    }

    private String generateContactUsSection(AppInfo appInfo) {
        String contact = "<h2>Contact Us</h2>\n" +
                "<p>If you have any questions or suggestions about our Privacy Policy, do not " +
                "hesitate to contact us at " + escapeHtml(appInfo.getContactEmail()) + ".";

        if (!appInfo.getWebsite().isEmpty()) {
            contact += " You may also visit our website at " + escapeHtml(appInfo.getWebsite()) + ".";
        }

        return contact + "</p>";
    }

    private String escapeHtml(String input) {
        if (input == null) return "";
        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}