/**
 * Copyright - See the COPYRIGHT that is included with this distribution.
 * EPICS pvData is distributed subject to a Software License Agreement found
 * in file LICENSE that is included with this distribution.
 */
package org.epics.nt;

import org.epics.pvdata.pv.Field;
import org.epics.pvdata.pv.Scalar;
import org.epics.pvdata.pv.ScalarType;
import org.epics.pvdata.pv.Structure;
import org.epics.pvdata.pv.Union;
import org.epics.pvdata.pv.PVField;
import org.epics.pvdata.pv.PVInt;
import org.epics.pvdata.pv.PVScalar;
import org.epics.pvdata.pv.PVStructure;
import org.epics.pvdata.pv.PVString;
import org.epics.pvdata.pv.PVStringArray;
import org.epics.pvdata.pv.PVUnion;
import org.epics.pvdata.property.PVTimeStamp;
import org.epics.pvdata.property.PVAlarm;

/**
 * Wrapper class for NTUnion.
 *
 * @author dgh
 */
public class NTUnion
    implements HasAlarm, HasTimeStamp
{
    public static final String URI = "epics:nt/NTUnion:1.0";

    /**
     * Creates an NTUnion wrapping the specified PVStructure if the latter is compatible.
     * <p>
     * Checks the supplied PVStructure is compatible with NTUnion
     * and if so returns an NTUnion which wraps it.
     * This method will return null if the structure is is not compatible
     * or is null.
     *
     * @param pvStructure the PVStructure to be wrapped
     * @return NTUnion instance on success, null otherwise
     */
    public static NTUnion wrap(PVStructure pvStructure)
    {
        if (!isCompatible(pvStructure))
            return null;
        return wrapUnsafe(pvStructure);
    }

    /**
     * Creates an NTUnion wrapping the specified PVStructure, regardless of the latter's compatibility.
     * <p>
     * No checks are made as to whether the specified PVStructure
     * is compatible with NTUnion or is non-null.
     * @param pvStructure the PVStructure to be wrapped
     * @return NTUnion instance
     */
    public static NTUnion wrapUnsafe(PVStructure pvStructure)
    {
        return new NTUnion(pvStructure);
    }

    /**
     * Returns whether the specified Structure reports to be a compatible NTUnion.
     * <p>
     * Checks if the specified Structure reports compatibility with this
     * version of NTUnion through its type ID, including checking version numbers.
     * The return value does not depend on whether the structure is actually
     * compatible in terms of its introspection type.
     *
     * @param structure the Structure to test
     * @return (false,true) if (is not, is) a compatible NTUnion
     */
    public static boolean is_a(Structure structure)
    {
        return NTUtils.is_a(structure.getID(), URI);
    }

    /**
     * Returns whether the specified PVStructure reports to be a compatible NTUnion.
     * <p>
     * Checks if the specified PVStructure reports compatibility with this
     * version of NTUnion through its type ID, including checking version numbers.
     * The return value does not depend on whether the structure is actually
     * compatible in terms of its introspection type.
     *
     * @param pvStructure the PVStructure to test
     * @return (false,true) if (is not, is) a compatible NTUnion
     */
    public static boolean is_a(PVStructure pvStructure)
    {
        return is_a(pvStructure.getStructure());
    }

    /**
     * Returns whether the specified Structure is compatible with NTUnion.
     * <p>
     * Checks if the specified Structure is compatible with this version
     * of NTUnion through the introspection interface.
     * @param structure the Structure to test
     * @return (false,true) if (is not, is) a compatible NTUnion
     */
    public static boolean isCompatible(Structure structure)
    {
        if (structure == null) return false;

        Union valueField = structure.getField(Union.class,"value");
        if (valueField == null)
            return false;

        NTField ntField = NTField.get();

        Field field = structure.getField("descriptor");
        if (field != null)
        {
            Scalar descriptorField = structure.getField(Scalar.class, "descriptor");
            if (descriptorField == null || descriptorField.getScalarType() != ScalarType.pvString)
                return false;
        }

        field = structure.getField("alarm");
        if (field != null && !ntField.isAlarm(field))
            return false;

        field = structure.getField("timeStamp");
        if (field != null && !ntField.isTimeStamp(field))
            return false;

        return true;
    }

    /**
     * Returns whether the specified PVStructure is compatible with NTUnion.
     * <p>
     * Checks if the specified PVStructure is compatible with this version
     * of NTUnion through the introspection interface
     * @param pvStructure the PVStructure to test
     * @return (false,true) if (is not, is) a compatible NTUnion
     */
    public static boolean isCompatible(PVStructure pvStructure)
    {
        if (pvStructure == null) return false;

        return isCompatible(pvStructure.getStructure());
    }

    /**
     * Returns whether the wrapped PVStructure is a valid NTUnion.
     * <p>
     * Unlike isCompatible(), isValid() may perform checks on the value
     * data as well as the introspection data.
     *
     * @return (false,true) if wrapped PVStructure (is not, is) a valid NTUnion
     */
    public boolean isValid()
    {
        return true;
    }

    /**
     * Creates an NTUnionBuilder instance.
     *
     * @return builder instance
     */
    public static NTUnionBuilder createBuilder()
    {
        return new NTUnionBuilder();
    }

    /**
     * Returns the PVStructure wrapped by this instance.
     *
     * @return the PVStructure wrapped by this instance
     */
    public PVStructure getPVStructure()
    {
        return pvNTUnion;
    }

    /**
     * Returns the value field.
     * 
     * @return the value field
     */
    public PVUnion getValue()
    {
        return pvValue;
    }

    /**
     * Returns the descriptor field.
     *
     * @return the descriptor field or null if no such field
     */
    public PVString getDescriptor()
    {
        return pvNTUnion.getSubField(PVString.class, "descriptor");
    }

    /* (non-Javadoc)
	 * @see org.epics.pvdata.nt.HasAlarm#getAlarm()
	 */
    public PVStructure getAlarm()
    {
       return pvNTUnion.getSubField(PVStructure.class, "alarm");
    }

    /* (non-Javadoc)
	 * @see org.epics.pvdata.nt.HasTimeStamp#getTimeStamp()
	 */
    public PVStructure getTimeStamp()
    {
        return pvNTUnion.getSubField(PVStructure.class, "timeStamp");
    }

    /* (non-Javadoc)
	 * @see org.epics.pvdata.nt.HasAlarm#attachAlarm(org.epics.pvdata.property.PVAlarm)
	 */
    public boolean attachAlarm(PVAlarm pvAlarm)
    {
        PVStructure al = getAlarm();
        if (al != null)
            return pvAlarm.attach(al);
        else
            return false;
    }

    /* (non-Javadoc)
	 * @see org.epics.pvdata.nt.HasTimeStamp#attachTimeStamp(org.epics.pvdata.property.PVTimeStamp)
	 */
    public boolean attachTimeStamp(PVTimeStamp pvTimeStamp)
    {
        PVStructure ts = getTimeStamp();
        if (ts != null)
            return pvTimeStamp.attach(ts);
        else
            return false;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */ 
    public String toString()
    {
        return getPVStructure().toString();
    }

    /**
     * Constructor.
     *
     * @param pvStructure the PVStructure to be wrapped
     */
    NTUnion(PVStructure pvStructure)
    {
        pvNTUnion = pvStructure;
        pvValue = pvStructure.getSubField(PVUnion.class, "value");
    }

    private PVStructure pvNTUnion;
    private PVUnion pvValue;
}

