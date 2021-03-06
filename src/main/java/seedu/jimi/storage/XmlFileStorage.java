package seedu.jimi.storage;

import javax.xml.bind.JAXBException;

import seedu.jimi.commons.exceptions.DataConversionException;
import seedu.jimi.commons.util.XmlUtil;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Stores Jimi's in an XML file
 */
public class XmlFileStorage {
    /**
     * Saves the given task book data to the specified file.
     */
    public static void saveDataToFile(File file, XmlSerializableTaskBook taskBook)
            throws FileNotFoundException {
        try {
            XmlUtil.saveDataToFile(file, taskBook);
        } catch (JAXBException e) {
            assert false : "Unexpected exception " + e.getMessage();
        }
    }

    /**
     * Returns task book in the file or an empty task book
     */
    public static XmlSerializableTaskBook loadDataFromSaveFile(File file) throws DataConversionException,
                                                                            FileNotFoundException {
        try {
            return XmlUtil.getDataFromFile(file, XmlSerializableTaskBook.class);
        } catch (JAXBException e) {
            throw new DataConversionException(e);
        }
    }

}
