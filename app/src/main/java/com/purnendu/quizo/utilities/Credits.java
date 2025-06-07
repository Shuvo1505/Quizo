package com.purnendu.quizo.utilities;

/**
 * A utility class providing methods related to application credits, specifically for generating
 * random links to asset creators. This is typically used to acknowledge the sources of
 * icons, illustrations, or other media used in the Quizo application.
 *
 * @author Purnendu Guha
 * @version 2.0.1
 */
//Class for Credits
public class Credits {
    /**
     * Generates and returns a random link from a predefined list of URLs.
     * These links typically point to the authors or sources of various assets (e.g., icons)
     * used within the application, providing proper attribution.
     *
     * @return A randomly selected URL string from the list of credits.
     */
    public static String generateRandomLink() {
        String[] links = {
                "https://www.flaticon.com/authors/freepik",
                "https://www.flaticon.com/authors/kerismaker",
                "https://www.flaticon.com/authors/pixelverse",
                "https://www.flaticon.com/authors/heykiyou",
                "https://www.flaticon.com/authors/vectorslab",
                "https://www.flaticon.com/authors/manshagraphics",
                "https://www.flaticon.com/authors/pixel-perfect",
                "https://www.flaticon.com/authors/aldo-cervantes",
                "https://www.flaticon.com/authors/andy-horvath",
                "https://www.flaticon.com/authors/triangle-squad",
                "https://www.flaticon.com/authors/roundicons"
        };
        return links[(int) (Math.random() * links.length)];
    }
}
