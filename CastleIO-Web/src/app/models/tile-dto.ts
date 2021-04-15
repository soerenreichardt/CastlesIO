interface TileContentMatrixDTO {
    rows: number;
    columns: number;
    values: string[];
}

interface TileLayoutDTO {
    rotation: number;
    content: TileContentMatrixDTO;
}

export interface TileDTO {
    id: number;
    tileLayout: TileLayoutDTO;
}
