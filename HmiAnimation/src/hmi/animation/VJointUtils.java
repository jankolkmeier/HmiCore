package hmi.animation;

import hmi.math.Quat4f;
import hmi.math.Vec3f;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

/**
 * Utilities for VJoints
 * @author hvanwelbergen, zwiers
 * 
 */
@Slf4j
public final class VJointUtils
{
    private VJointUtils()
    {
    }

    public static class VJointInCollectionPredicate implements Predicate<VJoint>
    {
        private final Collection<VJoint> jointCol;

        public VJointInCollectionPredicate(Collection<VJoint> collection)
        {
            this.jointCol = collection;
        }

        @Override
        public boolean apply(VJoint vj)
        {
            for (VJoint joint : jointCol)
            {
                if (joint.equivId(vj)) return true;
            }
            return false;
        }
    }

    /**
     * Get a collection of joints that intersects vj1 and vj2
     */
    public static Collection<VJoint> intersection(Collection<VJoint> vj1, Collection<VJoint> vj2)
    {
        List<VJoint> vjOut = new ArrayList<VJoint>(vj1);
        return Collections2.filter(vjOut, new VJointInCollectionPredicate(vj2));
    }

    /**
     * Create a set of sid strings from a collection of VJoints
     */
    public static Set<String> transformToSidSet(Collection<VJoint> joints)
    {
        Collection<String> j = Collections2.transform(joints, new Function<VJoint, String>()
        {
            @Override
            public String apply(VJoint joint)
            {
                return joint.getSid();
            }
        });
        return ImmutableSet.copyOf(j);
    }

    public static void setSidToIdOrNameIfNullSid(Collection<VJoint> joints)
    {
        for (VJoint vj : joints)
        {
            if (vj.getSid() == null)
            {
                if (vj.getId() != null)
                {
                    vj.setSid(vj.getId());
                }
                else if (vj.getName() != null)
                {
                    vj.setSid(vj.getName());
                }
            }
        }
    }

    /**
     * Create a set of sid strings from a collection of VJoints. If the sid of a joint in joints is null, its id or name are used respectively.
     */
    public static List<String> transformToSidList(List<VJoint> joints)
    {
        List<String> j = Lists.transform(joints, new Function<VJoint, String>()
        {
            @Override
            public String apply(VJoint joint)
            {
                if (joint.getSid() != null)
                {
                    return joint.getSid();
                }
                else if (joint.getId() != null)
                {
                    return joint.getId();
                }
                else
                {
                    return joint.getName();
                }
            }
        });
        return ImmutableList.copyOf(j);
    }

    /**
     * Tries to set the skeleton in the HAnim neutral pose.
     * It is assumed that joints have joint sids according to the HAnim standard.
     * 
     * Assumes the ankles are aligned correctly for 'stable' standing, re-aligns them so that the ankle stance is maintained.
     */
    public static void setHAnimPose(VJoint skeletonRoot)
    {
        // NB the order of setting the alignments is important: work from root to leaf nodes
        float[] downVec = Vec3f.getVec3f(0f, -1f, 0f);

        // left arms/hand/fingers:
        alignSegment(skeletonRoot, hmi.animation.Hanim.l_shoulder, hmi.animation.Hanim.l_elbow, downVec);
        alignSegment(skeletonRoot, hmi.animation.Hanim.l_elbow, hmi.animation.Hanim.l_wrist, downVec);

        alignSegment(skeletonRoot, hmi.animation.Hanim.l_index1, hmi.animation.Hanim.l_index2, downVec);
        alignSegment(skeletonRoot, hmi.animation.Hanim.l_index2, hmi.animation.Hanim.l_index3, downVec);
        alignSegment(skeletonRoot, hmi.animation.Hanim.l_index3, hmi.animation.Hanim.l_index_distal_tip, downVec);

        alignSegment(skeletonRoot, hmi.animation.Hanim.l_middle1, hmi.animation.Hanim.l_middle2, downVec);
        alignSegment(skeletonRoot, hmi.animation.Hanim.l_middle2, hmi.animation.Hanim.l_middle3, downVec);
        alignSegment(skeletonRoot, hmi.animation.Hanim.l_middle3, hmi.animation.Hanim.l_middle_distal_tip, downVec);

        alignSegment(skeletonRoot, hmi.animation.Hanim.l_ring1, hmi.animation.Hanim.l_ring2, downVec);
        alignSegment(skeletonRoot, hmi.animation.Hanim.l_ring2, hmi.animation.Hanim.l_ring3, downVec);
        alignSegment(skeletonRoot, hmi.animation.Hanim.l_ring3, hmi.animation.Hanim.l_ring_distal_tip, downVec);

        alignSegment(skeletonRoot, hmi.animation.Hanim.l_pinky1, hmi.animation.Hanim.l_pinky2, downVec);
        alignSegment(skeletonRoot, hmi.animation.Hanim.l_pinky2, hmi.animation.Hanim.l_pinky3, downVec);
        alignSegment(skeletonRoot, hmi.animation.Hanim.l_pinky3, hmi.animation.Hanim.l_pinky_distal_tip, downVec);

        // right arms/hand/fingers:
        alignSegment(skeletonRoot, hmi.animation.Hanim.r_shoulder, hmi.animation.Hanim.r_elbow, downVec);
        alignSegment(skeletonRoot, hmi.animation.Hanim.r_elbow, hmi.animation.Hanim.r_wrist, downVec);

        alignSegment(skeletonRoot, hmi.animation.Hanim.r_index1, hmi.animation.Hanim.r_index2, downVec);
        alignSegment(skeletonRoot, hmi.animation.Hanim.r_index2, hmi.animation.Hanim.r_index3, downVec);
        alignSegment(skeletonRoot, hmi.animation.Hanim.r_index3, hmi.animation.Hanim.r_index_distal_tip, downVec);

        alignSegment(skeletonRoot, hmi.animation.Hanim.r_middle1, hmi.animation.Hanim.r_middle2, downVec);
        alignSegment(skeletonRoot, hmi.animation.Hanim.r_middle2, hmi.animation.Hanim.r_middle3, downVec);
        alignSegment(skeletonRoot, hmi.animation.Hanim.r_middle3, hmi.animation.Hanim.r_middle_distal_tip, downVec);

        alignSegment(skeletonRoot, hmi.animation.Hanim.r_ring1, hmi.animation.Hanim.r_ring2, downVec);
        alignSegment(skeletonRoot, hmi.animation.Hanim.r_ring2, hmi.animation.Hanim.r_ring3, downVec);
        alignSegment(skeletonRoot, hmi.animation.Hanim.r_ring3, hmi.animation.Hanim.r_ring_distal_tip, downVec);

        alignSegment(skeletonRoot, hmi.animation.Hanim.r_pinky1, hmi.animation.Hanim.r_pinky2, downVec);
        alignSegment(skeletonRoot, hmi.animation.Hanim.r_pinky2, hmi.animation.Hanim.r_pinky3, downVec);
        alignSegment(skeletonRoot, hmi.animation.Hanim.r_pinky3, hmi.animation.Hanim.r_pinky_distal_tip, downVec);
        
        float[] thumbDir = Vec3f.getVec3f(0f, -1f, 1f);
        alignSegment(skeletonRoot, Hanim.l_thumb1, Hanim.l_thumb2, thumbDir);
        alignSegment(skeletonRoot, Hanim.l_thumb2, Hanim.l_thumb3, thumbDir);
        alignSegment(skeletonRoot, Hanim.l_thumb3, Hanim.l_thumb_distal_tip, thumbDir);
        
        alignSegment(skeletonRoot, Hanim.r_thumb1, Hanim.r_thumb2, thumbDir);
        alignSegment(skeletonRoot, Hanim.r_thumb2, Hanim.r_thumb3, thumbDir);
        alignSegment(skeletonRoot, Hanim.r_thumb3, Hanim.r_thumb_distal_tip, thumbDir);

        // legs/feet:
        // align hip-knee with downvec, but correct "backwards" at the ankle joints (so we use alignIsolatedSegments rather than alignSegment)
        // so, we are assuming that ankles/feet were correctly positioned on the ground already, and we don't want to change anything there.
        // For Armandia, this takes into account the stilleto "high heels" position of the feet.
        alignIsolatedSegments(skeletonRoot, hmi.animation.Hanim.l_hip, hmi.animation.Hanim.l_knee, hmi.animation.Hanim.l_ankle, downVec);
        alignIsolatedSegments(skeletonRoot, hmi.animation.Hanim.r_hip, hmi.animation.Hanim.r_knee, hmi.animation.Hanim.r_ankle, downVec);
    }

    /** 
     * auxiliary method for aligning some body segment inside a skeleton structure with a specified direction vector
     * The parent joint is rotated, the grandchild joint is rotated an equal amount backwards, so that just the segments,
     * starting at parent, up to but not including grandchild are rotated "in isolation"
     */
    private static void alignIsolatedSegments(VJoint skeletonRoot, String parentSid, String childSid, String grandchildSid, float[] vec)
    {
        VJoint parent = skeletonRoot.getPartBySid(parentSid);
        VJoint child = skeletonRoot.getPartBySid(childSid);
        VJoint grandchild = skeletonRoot.getPartBySid(grandchildSid);
        if (parent != null && child != null)
        {
            float[] a = child.getRelativePositionFrom(parent);
            float[] q = Quat4f.getFromVectors(a, vec);
            float[] r = Quat4f.getQuat4f();
            parent.getPathRotation(null, r);
            float[] rinv = Quat4f.getQuat4f();
            Quat4f.conjugate(rinv, r);
            float[] s = Quat4f.getQuat4f();

            Quat4f.mul(s, rinv, q);
            Quat4f.mul(s, r);
            parent.rotate(s);
            Quat4f.inverse(s);
            grandchild.rotate(s);
        }
        else
        {
            log.warn("No " + parentSid + " or " + childSid + " for skeleton " + skeletonRoot.getName());
        }
    }

    /** 
     * auxiliary method for aligning some body segment inside a skeleton structure with a specified direction vector 
     * The parent joint is rotated 
     **/
    public static void alignSegment(VJoint skeletonRoot, String parentSid, String childSid, float[] vec)
    {
        VJoint parent = skeletonRoot.getPartBySid(parentSid);
        VJoint child = skeletonRoot.getPartBySid(childSid);
        if (parent != null && child != null)
        {
            float[] a = child.getRelativePositionFrom(parent);
            float[] q = Quat4f.getFromVectors(a, vec);

            float[] r = Quat4f.getQuat4f();
            parent.getPathRotation(null, r);
            float[] rinv = Quat4f.getQuat4f();
            Quat4f.conjugate(rinv, r);
            float[] s = Quat4f.getQuat4f();

            Quat4f.mul(s, rinv, q);
            Quat4f.mul(s, r);
            parent.insertRotation(s); // post multiplies parent rotation with s.

        }
        else
        {
            log.warn("No " + parentSid + " or " + childSid + " for skeleton " + skeletonRoot.getName());
        }
    }
}
