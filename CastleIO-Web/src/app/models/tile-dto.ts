interface TileContentMatrixDTO {
    rows: number;
    columns: number;
    content: string[];
}

interface TileLayoutDTO {
    rotation: number;
    content: TileContentMatrixDTO;
}

export interface TileDTO {
    id: number;
    tileLayout: TileLayoutDTO;
}
