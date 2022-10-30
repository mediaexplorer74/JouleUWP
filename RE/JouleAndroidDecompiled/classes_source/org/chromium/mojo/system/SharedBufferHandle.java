package org.chromium.mojo.system;

import java.nio.ByteBuffer;

public interface SharedBufferHandle extends Handle {

    public static class CreateOptions {
        private CreateFlags mFlags;

        public CreateOptions() {
            this.mFlags = CreateFlags.NONE;
        }

        public CreateFlags getFlags() {
            return this.mFlags;
        }
    }

    public static class DuplicateOptions {
        private DuplicateFlags mFlags;

        public DuplicateOptions() {
            this.mFlags = DuplicateFlags.NONE;
        }

        public DuplicateFlags getFlags() {
            return this.mFlags;
        }
    }

    public static class CreateFlags extends Flags<CreateFlags> {
        private static final int FLAG_NONE = 0;
        public static final CreateFlags NONE;

        static {
            NONE = (CreateFlags) none().immutable();
        }

        protected CreateFlags(int flags) {
            super(flags);
        }

        public static CreateFlags none() {
            return new CreateFlags(0);
        }
    }

    public static class DuplicateFlags extends Flags<DuplicateFlags> {
        private static final int FLAG_NONE = 0;
        public static final DuplicateFlags NONE;

        static {
            NONE = (DuplicateFlags) none().immutable();
        }

        protected DuplicateFlags(int flags) {
            super(flags);
        }

        public static DuplicateFlags none() {
            return new DuplicateFlags(0);
        }
    }

    public static class MapFlags extends Flags<MapFlags> {
        private static final int FLAG_NONE = 0;
        public static final MapFlags NONE;

        static {
            NONE = (MapFlags) none().immutable();
        }

        protected MapFlags(int flags) {
            super(flags);
        }

        public static MapFlags none() {
            return new MapFlags(0);
        }
    }

    SharedBufferHandle duplicate(DuplicateOptions duplicateOptions);

    ByteBuffer map(long j, long j2, MapFlags mapFlags);

    SharedBufferHandle pass();

    void unmap(ByteBuffer byteBuffer);
}
