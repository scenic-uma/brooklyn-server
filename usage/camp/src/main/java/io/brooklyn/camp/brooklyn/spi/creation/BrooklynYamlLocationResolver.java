package io.brooklyn.camp.brooklyn.spi.creation;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import brooklyn.location.Location;
import brooklyn.location.LocationDefinition;
import brooklyn.management.ManagementContext;
import brooklyn.util.collections.MutableList;
import brooklyn.util.exceptions.Exceptions;
import brooklyn.util.text.Strings;

import com.google.common.collect.Iterables;

public class BrooklynYamlLocationResolver {

    protected final ManagementContext mgmt;

    public BrooklynYamlLocationResolver(ManagementContext bmc) {
        this.mgmt = bmc;
    }

    /** returns list of locations, if any were supplied, or null if none indicated */
    @SuppressWarnings("unchecked")
    public List<Location> resolveLocations(Map<? super String,?> attrs, boolean removeUsedAttributes) {
        Object location = attrs.get("location");
        Object locations = attrs.get("locations");

        if (location==null && locations==null)
            return null;
        
        Location locationFromString = null;
        List<Location> locationsFromList = null;
        
        if (location!=null) {
            if (!(location instanceof String))
                throw new IllegalStateException("Illegal parameter for 'location' ("+location+"); must be a string");
            locationFromString = resolveLocationFromString((String)location);
        }
        
        if (locations!=null) {
            if (!(locations instanceof Iterable))
                throw new IllegalStateException("Illegal parameter for 'locations' ("+locations+"); must be an iterable");
            locationsFromList = resolveLocations( (Iterable<Object>)locations );
        }
        
        if (locationFromString!=null && locationsFromList!=null) {
            if (locationsFromList.size() != 1)
                throw new IllegalStateException("Conflicting 'location' and 'locations' ("+location+" and "+locations+"); "
                    + "if both are supplied the list must have exactly one element being the same");
            if (!locationFromString.equals( Iterables.getOnlyElement(locationsFromList) ))
                throw new IllegalStateException("Conflicting 'location' and 'locations' ("+location+" and "+locations+"); "
                    + "different location specified in each");
        } else if (locationFromString!=null) {
            locationsFromList = Arrays.asList(locationFromString);
        }
        
        return locationsFromList;
    }

    public List<Location> resolveLocations(Iterable<Object> locations) {
        List<Location> result = MutableList.of();
        for (Object l: locations) {
            Location ll = resolveLocation(l);
            if (ll!=null) result.add(ll);
        }
        return result;
    }

    public Location resolveLocation(Object location) {
        if (location instanceof String)
            return resolveLocationFromString((String)location);
        // could support e.g. location definition
        throw new IllegalStateException("Illegal parameter for 'location' ("+location+"); must be a string");
    }
    
    /** resolves the location from the given spec string, either "Named Location", or "named:Named Location" format;
     * returns null if input is blank (or null); otherwise guaranteed to resolve or throw error */
    public Location resolveLocationFromString(String location) {
        if (Strings.isBlank(location))
            return null;
        
        LocationDefinition ldef = mgmt.getLocationRegistry().getDefinedLocationByName((String)location);
        if (ldef!=null)
            // found it as a named location
            return mgmt.getLocationRegistry().resolve(ldef);
        
        if (mgmt.getLocationRegistry().canMaybeResolve((String)location)) {
            try {
                return mgmt.getLocationRegistry().resolve((String)location);
            } catch (Exception e) {
                Exceptions.propagateIfFatal(e);
                throw new IllegalStateException("Illegal parameter for 'location' ("+location+"); not resolvable: "+e, e);
            }
        }
        throw new IllegalStateException("Illegal parameter for 'location' ("+location+"); not resolvable");
    }


}