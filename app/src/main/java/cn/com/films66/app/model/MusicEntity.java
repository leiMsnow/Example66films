package cn.com.films66.app.model;

import java.util.List;

/**
 * Created by zhangleilei on 21/02/2017.
 */

public class MusicEntity {

    public ExternalIdsEntity external_ids;
    public int play_offset_ms;
    public ExternalMetadataEntity external_metadata;
    public String title;
    public String release_date;
    public String label;
    public String duration_ms;
    public AlbumEntityXXXX album;
    public String acrid;
    public int result_from;
    public List<ArtistsEntityXXXX> artists;

    public static class ExternalIdsEntity {
        public String isrc;
        public String upc;
    }

    public static class ExternalMetadataEntity {
        public OmusicEntity omusic;
        public YoutubeEntity youtube;
        public DeezerEntity deezer;
        public ItunesEntity itunes;
        public SpotifyEntity spotify;

        public static class OmusicEntity {
            public AlbumEntity album;
            public TrackEntity track;
            public List<ArtistsEntity> artists;

            public static class AlbumEntity {
                public String name;
                public int id;

            }

            public static class TrackEntity {
                public String name;
                public int id;
            }

            public static class ArtistsEntity {
                public String name;
                public int id;

            }
        }

        public static class YoutubeEntity {
            public String vid;
        }

        public static class DeezerEntity {

            public AlbumEntityX album;
            public TrackEntityX track;
            public List<ArtistsEntityX> artists;


            public static class AlbumEntityX {
                public String id;
            }

            public static class TrackEntityX {
                public String id;
            }

            public static class ArtistsEntityX {
                public String id;
            }
        }

        public static class ItunesEntity {
            public AlbumEntityXX album;
            public TrackEntityXX track;
            public List<ArtistsEntityXX> artists;

            public static class AlbumEntityXX {
                public int id;
            }

            public static class TrackEntityXX {
                public int id;
            }

            public static class ArtistsEntityXX {
                public int id;
            }
        }

        public static class SpotifyEntity {
            public AlbumEntityXXX album;
            public TrackEntityXXX track;
            public List<ArtistsEntityXXX> artists;

            public static class AlbumEntityXXX {
                public String id;
            }

            public static class TrackEntityXXX {
                public String id;
            }

            public static class ArtistsEntityXXX {
                public String id;
            }
        }
    }

    public static class AlbumEntityXXXX {
        public String name;
    }

    public static class ArtistsEntityXXXX {
        public String name;
    }
}
